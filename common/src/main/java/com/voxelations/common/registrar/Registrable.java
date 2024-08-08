package com.voxelations.common.registrar;

/**
 * Things that are able to be (un)registered.
 */
public interface Registrable {

    default void enable() {
    }

    default void disable() {
    }
}
