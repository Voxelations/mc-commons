package com.voxelations.common.platform.paper;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.voxelations.common.platform.paper.registrar.PaperRegistrar;
import com.voxelations.common.platform.paper.util.Schedulers;
import com.voxelations.common.registrar.ContainerProcessor;
import com.voxelations.common.registrar.PluginMetadata;
import com.voxelations.common.registrar.Registrar;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class PaperPluginModule extends AbstractModule {

    private final PaperPlugin plugin;

    @Provides
    @Singleton
    public LegacyPaperCommandManager<CommandSender> provideCommandManager() {
        LegacyPaperCommandManager<CommandSender> commandManager = LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.asyncCoordinator());

        // Capabilities
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

        return commandManager;
    }

    @Provides
    @Singleton
    public AnnotationParser<CommandSender> provideAnnotationParser(LegacyPaperCommandManager<CommandSender> commandManager) {
        return new AnnotationParser<>(commandManager, CommandSender.class);
    }

    @Provides
    @Singleton
    public Schedulers provideSchedulers(PaperPlugin plugin) {
        return new Schedulers(plugin);
    }

    @Override
    protected void configure() {
        // Platform related
        bind(Registrar.class).to(PaperRegistrar.class);

        // Plugin related
        bind(PaperPlugin.class).toInstance(plugin);
        bind(PluginMetadata.class).toInstance(plugin);

        // Containers
        Multibinder<Object> binder = Multibinder.newSetBinder(binder(), Object.class);
        try (InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(ContainerProcessor.PATH)) {
            if (inputStream == null) return;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                reader.lines().distinct().map(it -> {
                    try {
                        return Class.forName(it, false, plugin.getClass().getClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).forEach(clazz -> binder.addBinding().to(clazz));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
