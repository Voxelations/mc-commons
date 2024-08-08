package com.voxelations.common.event;

import java.util.function.Consumer;

public interface EventHandler<T> extends Consumer<T>, Comparable<EventHandler<T>> {

    /**
     * @return the priority
     */
    int getPriority();

    @Override
    default int compareTo(EventHandler<T> other) {
        return Integer.compare(getPriority(), other.getPriority());
    }
}
