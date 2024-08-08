package com.voxelations.common.event;

/**
 * An event that can be cancelled.
 */
public interface Cancellable {

    /**
     * @return {@code true} if the event has been cancelled
     */
    boolean isCancelled();

    /**
     * @param cancel {@code true} if the event is cancelled
     */
    void setCancelled(boolean cancel);
}
