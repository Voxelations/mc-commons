package com.voxelations.common.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voxelations.common.config.types.DurationSerializer;
import com.voxelations.common.config.types.KeySerializer;
import com.voxelations.common.config.types.SoundSerializer;
import com.voxelations.common.config.types.TitleTimesSerializer;
import com.voxelations.common.registrar.Registrable;
import com.voxelations.common.util.TypeAware;
import io.leangen.geantyref.TypeToken;
import lombok.SneakyThrows;
import org.spongepowered.configurate.reference.WatchServiceListener;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class ConfigService implements Registrable {

    private final TypeSerializerCollection.Builder serializers = TypeSerializerCollection.defaults().childBuilder();
    private final Map<Class<?>, ConfigHandler<?>> configs = new ConcurrentHashMap<>();
    private final WatchServiceListener fileWatcher = WatchServiceListener.builder()
            .taskExecutor(Executors.newSingleThreadExecutor())
            .threadFactory(new ThreadFactoryBuilder().setNameFormat(getClass().getName() + "-%d").build())
            .build();

    public ConfigService() throws IOException {
    }

    @Override
    public void enable() {
        // Register default serializers
        register(new DurationSerializer());
        register(new KeySerializer());
        register(new SoundSerializer());
        register(new TitleTimesSerializer());
    }

    @Override
    public void disable() {
        configs.values().forEach(ConfigHandler::close);
        try {
            fileWatcher.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a config for the given class.
     *
     * @param dataDirectory The data directory
     * @param clazz The class
     */
    @SneakyThrows
    public void register(Path dataDirectory, Class<?> clazz) {
        if (!Files.exists(dataDirectory)) Files.createDirectories(dataDirectory);

        configs.put(clazz, new ConfigHandler<>(fileWatcher, serializers, dataDirectory, clazz));
    }

    /**
     * Registers a type serializer.
     *
     * @param serializer The serializer
     */
    @SuppressWarnings("unchecked")
    public <T> void register(TypeSerializer<T> serializer) {
        if (serializer instanceof ScalarSerializer<T> scalarSerializer) {
            serializers.register(scalarSerializer);
            return;
        }

        if (serializer instanceof TypeAware<?> typeAware) {
            serializers.register(((TypeToken<T>) typeAware.getType()), serializer);
        }
    }

    /**
     * Gets the raw config handler for the given class.
     *
     * @param clazz The class
     * @param <T> The type
     * @return The config handler
     */
    @SuppressWarnings("unchecked")
    public <T> ConfigHandler<T> getRaw(Class<T> clazz) {
        return (ConfigHandler<T>) configs.get(clazz);
    }

    /**
     * Gets the config for the given class.
     *
     * @param clazz The class
     * @param <T> The type
     * @return The config
     */
    public <T> T get(Class<T> clazz) {
        return getRaw(clazz).get();
    }
}
