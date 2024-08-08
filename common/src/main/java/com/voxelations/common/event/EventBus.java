package com.voxelations.common.event;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus {

    private final Map<Class<?>, Set<EventHandler<Object>>> listeners = new ConcurrentHashMap<>();

    /**
     * Registers a listener for the given event class.
     *
     * @param eventClass The event class
     * @param listener The listener
     * @param <T> The event type
     */
    @SuppressWarnings("unchecked")
    public <T> void register(Class<T> eventClass, EventHandler<T> listener) {
        listeners.computeIfAbsent(
                eventClass,
                $ -> new TreeSet<>(Comparator.<EventHandler<Object>>comparingInt(EventHandler::getPriority).thenComparingLong(System::identityHashCode))
        ).add((EventHandler<Object>) listener);
    }

    /**
     * Registers a listener for the given event class.
     *
     * @param eventClass The event class
     * @param priority The priority
     * @param listener The listener
     * @param <T> The event type
     *
     * @see #register(Class, EventHandler)
     */
    public <T> EventHandler<T> register(Class<T> eventClass, int priority, Consumer<T> listener) {
        EventHandler<T> eventHandler = new EventHandler<>() {
            @Override
            public void accept(T event) {
                listener.accept(event);
            }

            @Override
            public int getPriority() {
                return priority;
            }
        };

        register(eventClass, eventHandler);

        return eventHandler;
    }

    /**
     * Unregisters a listener for the given event class.
     *
     * @param eventClass The event class
     * @param listener The listener
     * @param <T> The event type
     */
    public <T> void unregister(Class<T> eventClass, EventHandler<T> listener) {
        Optional.ofNullable(listeners.get(eventClass))
                .ifPresent(listeners -> listeners.removeIf(consumer -> consumer == listener));
    }

    /**
     * Posts an event.
     *
     * @param event The event
     * @param <T> The event type
     * @return The event, after all listeners have been processed
     */
    public <T> T post(T event) {
        Optional.ofNullable(listeners.get(event.getClass()))
                .ifPresent(listeners -> listeners.forEach(listener -> listener.accept(event)));

        return event;
    }
}

