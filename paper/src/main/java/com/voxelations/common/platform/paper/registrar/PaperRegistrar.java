package com.voxelations.common.platform.paper.registrar;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.voxelations.common.config.ConfigService;
import com.voxelations.common.platform.paper.PaperPlugin;
import com.voxelations.common.registrar.CloudCommand;
import com.voxelations.common.registrar.PluginMetadata;
import com.voxelations.common.registrar.Registrar;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.incendo.cloud.annotations.AnnotationParser;

import java.util.Set;

public class PaperRegistrar extends Registrar {

    private final Provider<Set<Object>> containers;
    private final Provider<PaperPlugin> plugin;
    private final Provider<AnnotationParser<CommandSender>> annotationParser;

    @Inject
    public PaperRegistrar(Provider<PluginMetadata> pluginMetadata, Provider<ConfigService> configService, Provider<Set<Object>> containers, Provider<PaperPlugin> plugin, Provider<AnnotationParser<CommandSender>> annotationParser) {
        super(pluginMetadata, configService, containers);
        this.containers = containers;
        this.plugin = plugin;
        this.annotationParser = annotationParser;
    }

    @Override
    public void enable() {
        super.enable();

        // Register platform adapted containers
        containers.get().forEach(container -> {
            if (container instanceof Listener listener) plugin.get().getServer().getPluginManager().registerEvents(listener, plugin.get());
            if (container instanceof CloudCommand command) annotationParser.get().parse(command);
        });
    }
}
