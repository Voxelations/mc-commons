package com.voxelations.common.util;

import io.leangen.geantyref.TypeToken;

/**
 * Things that are aware of its type.
 *
 * @param <T> The type
 */
public interface TypeAware<T> {

    /**
     * Gets the type.
     *
     * @return The type
     */
    TypeToken<T> getType();
}
