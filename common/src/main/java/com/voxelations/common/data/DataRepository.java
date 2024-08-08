package com.voxelations.common.data;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Generic repository for entities.
 *
 * @param <ID> the type of the id
 * @param <T> the type of the entity
 */
public interface DataRepository<ID, T> {

    /**
     * Finds all the entities.
     *
     * @return all the entities
     */
    List<T> findAll();

    /**
     * Finds an entity by its id.
     *
     * @param id the id of the entity
     * @return the entity with the given id, or null if it doesn't exist
     */
    @Nullable
    T findById(ID id);

    /**
     * Saves an entity.
     *
     * @param entity the entity to save
     */
    void save(T entity);

    /**
     * Saves a collection of entities.
     *
     * @param entities the entities to save
     */
    void saveAll(Collection<T> entities);

    /**
     * Deletes an entity by its id.
     *
     * @param id the id of the entity to delete
     */
    void deleteById(ID id);
}
