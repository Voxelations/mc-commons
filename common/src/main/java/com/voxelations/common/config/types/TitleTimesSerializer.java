package com.voxelations.common.config.types;

import com.voxelations.common.util.TypeAware;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Objects;

public class TitleTimesSerializer implements TypeAware<Title.Times>, TypeSerializer<Title.Times> {

    @Override
    public TypeToken<Title.Times> getType() {
        return TypeToken.get(Title.Times.class);
    }

    @Override
    public Title.Times deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return Title.Times.times(
                Objects.requireNonNull(node.node("fade-in").get(Duration.class)),
                Objects.requireNonNull(node.node("stay").get(Duration.class)),
                Objects.requireNonNull(node.node("fade-out").get(Duration.class))
        );
    }

    @Override
    public void serialize(Type type, Title.@Nullable Times obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("fade-in").set(Duration.class, obj.fadeIn());
        node.node("stay").set(Duration.class, obj.stay());
        node.node("fade-out").set(Duration.class, obj.fadeOut());
    }
}
