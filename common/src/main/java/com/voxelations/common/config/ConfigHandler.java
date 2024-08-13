package com.voxelations.common.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.reference.WatchServiceListener;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

@Slf4j
public class ConfigHandler<T> implements AutoCloseable {

    private final Class<T> clazz;
    private final ConfigurationReference<CommentedConfigurationNode> base;
    private final ValueReference<T, CommentedConfigurationNode> config;

    @SneakyThrows
    public ConfigHandler(WatchServiceListener fileWatcher, TypeSerializerCollection.Builder serializers, Path dataDirectory, Class<T> clazz) {
        this.clazz = clazz;

        String name = clazz.getSimpleName().toLowerCase().replace("container", "").replace("feature", "") + ".yml";
        Path configFile = dataDirectory.resolve(name);

        this.base = fileWatcher.listenToConfiguration(
                file -> YamlConfigurationLoader.builder()
                        .nodeStyle(NodeStyle.BLOCK)
                        .defaultOptions(
                                opts -> opts
                                        .shouldCopyDefaults(true)
                                        .implicitInitialization(false)
                                        .serializers(it -> it.registerAll(serializers.build()))
                        )
                        .path(file)
                        .build(),
                configFile
        );
        this.config = base.referenceTo(clazz);

        base.save();
        fileWatcher.listenToFile(
                configFile,
                event -> log.info("Updated config: {}/{}", dataDirectory.getFileName().toString(), name)
        );
    }

    /**
     * Gets the config.
     *
     * @return the config
     */
    public T get() {
        return config.get();
    }

    /**
     * Saves the config.
     *
     * @throws ConfigurateException if the config cannot be saved
     */
    public void save() throws ConfigurateException {
        base.node().set(clazz, clazz.cast(get()));
        base.loader().save(base.node());
    }

    @Override
    public void close() {
        base.close();
    }
}
