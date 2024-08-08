package com.voxelations.common.data;

import com.voxelations.common.registrar.Registrable;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordUnmapper;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Generic repository that uses jooq to warehouse entities.
 * Note: expects 'id' as the entity id
 *
 * @param <ID> the type of the id
 * @param <T> the type of the entity
 */
public class JooqCrudRepository<ID, T> implements DataRepository<ID, T>, Registrable {

    private final DSLContext dsl;
    private final Table<Record> table;
    private final Consumer<DSLContext> create;
    private final RecordMapper<Record, T> recordMapper;
    private final RecordUnmapper<T, Record> recordUnmapper;

    public JooqCrudRepository(DSLContext dsl, Table<Record> table, Consumer<DSLContext> create, RecordMapper<Record, T> recordMapper, RecordUnmapper<T, Record> recordUnmapper) {
        this.dsl = dsl;
        this.table = table;
        this.create = create;
        this.recordMapper = recordMapper;
        this.recordUnmapper = recordUnmapper;
    }

    @Override
    public void enable() {
        // Create the tables
        create.accept(dsl);
    }

    @Override
    public List<T> findAll() {
        return dsl.selectFrom(table).fetch(recordMapper);
    }

    @Override
    @Nullable
    public T findById(ID id) {
        return dsl.selectFrom(table)
                .where(DSL.field("id").eq(id))
                .limit(1)
                .fetchOne(recordMapper);
    }

    @Override
    public void save(T entity) {
        Record record = recordUnmapper.unmap(entity);
        dsl.insertInto(table)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute();
    }

    @Override
    public void saveAll(Collection<T> entities) {
        Query[] queries = new Query[entities.size()];

        int i = 0;
        for (T entity : entities) {
            Record record = recordUnmapper.unmap(entity);
            Query update = dsl.insertInto(table)
                    .set(record)
                    .onDuplicateKeyUpdate()
                    .set(record);

            queries[i++] = update;
        }

        dsl.batch(queries).execute();
    }

    @Override
    public void deleteById(ID id) {
        dsl.deleteFrom(table)
                .where(DSL.field("id").eq(id))
                .limit(1)
                .execute();
    }
}
