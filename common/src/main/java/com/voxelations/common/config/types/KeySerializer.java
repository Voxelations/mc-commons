package com.voxelations.common.config.types;

import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class KeySerializer extends ScalarSerializer<Key> {

    public KeySerializer() {
        super(Key.class);
    }

    @Override
    public Key deserialize(Type type, Object obj) throws SerializationException {
        @Subst("key") String string = obj.toString();

        try {
            return Key.key(string);
        } catch (InvalidKeyException e) {
            throw new SerializationException(type, "Invalid Key: `%s`".formatted(string), e);
        }
    }

    @Override
    protected Object serialize(Key item, Predicate<Class<?>> typeSupported) {
        return item.asString();
    }
}
