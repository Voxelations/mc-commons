package com.voxelations.common.data;

public interface DataEntity {

    /**
     * @return {@code true} if dirty
     */
    boolean isDirty();

    /**
     * @param dirty The dirty state
     */
    void setDirty(boolean dirty);

    /**
     * Marks the entity as dirty. Equivalent to {@link #setDirty(boolean)} with {@code true}.
     */
    default void markDirty() {
        setDirty(true);
    }
}
