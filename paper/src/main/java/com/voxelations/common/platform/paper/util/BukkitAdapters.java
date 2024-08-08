package com.voxelations.common.platform.paper.util;

import com.voxelations.common.util.FinePosition;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.function.Function;

@UtilityClass
public class BukkitAdapters {

    public Adapter<FinePosition, Location> FINE_POSITION = Adapter.adapter(
            pos -> new Location(Bukkit.getWorld(pos.world()), pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch()),
            loc -> new FinePosition(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch())
    );

    /**
     * Adapter for converting between two types.
     *
     * @param <C> The complex type
     * @param <B> The bukkit type
     */
    public interface Adapter<C, B> {
        /**
         * Converts a complex type to a bukkit type.
         *
         * @param c The complex type
         * @return The bukkit type
         */
        B toBukkit(C c);

        /**
         * Converts a bukkit type to a complex type.
         *
         * @param b The bukkit type
         * @return The complex type
         */
        C toComplex(B b);

        static <C, B> Adapter<C, B> adapter(Function<C, B> toBukkit, Function<B, C> toComplex) {
            return new Adapter<>() {
                @Override
                public B toBukkit(C c) {
                    return toBukkit.apply(c);
                }

                @Override
                public C toComplex(B b) {
                    return toComplex.apply(b);
                }
            };
        }
    }
}
