package com.voxelations.common.registrar;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.voxelations.common.config.ConfigService;
import com.voxelations.common.util.TypeAware;
import lombok.extern.slf4j.Slf4j;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Set;

@Slf4j
public class Registrar implements Registrable {

    private final Provider<PluginMetadata> pluginMetadata;
    private final Provider<ConfigService> configService;
    private final Provider<Set<Object>> containers;

    @Inject
    public Registrar(Provider<PluginMetadata> pluginMetadata, Provider<ConfigService> configService, Provider<Set<Object>> containers) {
        this.pluginMetadata = pluginMetadata;
        this.configService = configService;
        this.containers = containers;
    }


    @Override
    @OverridingMethodsMustInvokeSuper
    public void enable() {
        // Enable all the containers
        containers.get().forEach(container -> {
            if (container instanceof Registrable registrable) registrable.enable();
            if (container.getClass().isAnnotationPresent(ConfigSerializable.class)) configService.get().register(pluginMetadata.get().getDataDirectory(), container.getClass());
            if (container instanceof ScalarSerializer<?> serializer) configService.get().register(serializer);
            if (container instanceof TypeSerializer<?> serializer) {
                if (!(serializer instanceof TypeAware<?>)) {
                    log.error("TypeSerializer {} is not TypeAware", container.getClass());
                    return;
                }

                configService.get().register(serializer);
            }
        });
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void disable() {
        // Disable all the containers
        containers.get().forEach(container -> {
            if (container instanceof Registrable registrable) registrable.disable();
            if (container instanceof AutoCloseable closeable) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    log.error("Failed to close container", e);
                }
            }
        });
    }
}
