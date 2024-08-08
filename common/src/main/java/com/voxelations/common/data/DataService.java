package com.voxelations.common.data;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/**
 * Generic service for entities.
 *
 * @param <ID> the type of the id
 * @param <T> the type of the entity
 */
public interface DataService<ID, T> {

    /**
     * Resolves an entity. This can be blocking.
     *
     * @param id the id of the entity
     * @return the entity with the given id, or null if it doesn't exist
     */
    @Nullable
    T resolve(ID id);

    /**
     * @return the cache
     */
    Map<ID, T> getCache();

    /**
     * @return the repository
     */
    DataRepository<ID, T> getRepository();
}
