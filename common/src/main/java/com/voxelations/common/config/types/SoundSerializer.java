package com.voxelations.common.config.types;

import com.voxelations.common.util.TypeAware;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.OptionalLong;

public class SoundSerializer implements TypeAware<Sound>, TypeSerializer<Sound> {

    @Override
    public TypeToken<Sound> getType() {
        return TypeToken.get(Sound.class);
    }

    @Override
    public Sound deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Sound.Builder builder = Sound.sound();

        Key soundType = node.node("type").get(Key.class);
        if (soundType == null) throw new SerializationException(node, Sound.class, "Required field `type` is missing");
        builder.type(soundType);

        Sound.Source source = node.node("source").get(Sound.Source.class, Sound.Source.MASTER);
        builder.source(source);

        float volume = node.node("volume").getFloat(1.0f);
        builder.volume(volume);

        float pitch = node.node("pitch").getFloat(1.0f);
        builder.pitch(pitch);

        ConfigurationNode seed = node.node("seed");
        if (!seed.virtual()) builder.seed(OptionalLong.of(seed.getLong()));

        return builder.build();
    }

    @Override
    public void serialize(Type type, @Nullable Sound obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("type").set(Key.class, obj.name());
        node.node("source").set(Key.class, obj.source().name());
        node.node("volume").set(Key.class, obj.volume());
        node.node("pitch").set(Key.class, obj.pitch());

        if (obj.seed().isPresent()) node.node("seed").set(Key.class, obj.seed().getAsLong());
        else node.node("seed").set(null);
    }
}
