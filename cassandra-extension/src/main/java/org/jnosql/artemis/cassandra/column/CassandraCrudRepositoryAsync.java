package org.jnosql.artemis.cassandra.column;


import com.datastax.driver.core.ConsistencyLevel;
import org.jnosql.artemis.CrudRepositoryAsync;
import org.jnosql.diana.api.ExecuteAsyncQueryException;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * A Cassandra extension of {@link CrudRepositoryAsync}
 */
public interface CassandraCrudRepositoryAsync<T> extends CrudRepositoryAsync<T> {

    /**
     * Save the entity with ConsistencyLevel
     *
     * @param <T>    the type
     * @param entity the entity
     * @param level  {@link ConsistencyLevel}
     */
    <T> void save(T entity, ConsistencyLevel level);

    /**
     * Saves the entity with ConsistencyLevel
     *
     * @param entity the entity
     * @param ttl    the ttl
     * @param level  {@link ConsistencyLevel}
     * @throws ExecuteAsyncQueryException
     * @throws UnsupportedOperationException
     */
    void save(T entity, Duration ttl, ConsistencyLevel level) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Save the entity with ConsistencyLevel
     *
     * @param entities the entities
     * @param level    {@link ConsistencyLevel}
     */
    void save(Iterable<T> entities, ConsistencyLevel level);

    /**
     * Saves the entity with ConsistencyLevel
     *
     * @param entities the entities
     * @param ttl      the ttl
     * @param level    {@link ConsistencyLevel}
     * @throws ExecuteAsyncQueryException
     * @throws UnsupportedOperationException
     */
    void save(Iterable<T> entities, Duration ttl, ConsistencyLevel level) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Save the entity with ConsistencyLevel
     *
     * @param callBack the callBack
     * @param entity   the entity
     * @param level    {@link ConsistencyLevel}
     */
    void save(T entity, ConsistencyLevel level, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Saves the entity with ConsistencyLevel
     *
     * @param callBack the callBack
     * @param entity   the entity
     * @param ttl      the ttl
     * @param level    {@link ConsistencyLevel}
     * @throws ExecuteAsyncQueryException
     * @throws UnsupportedOperationException
     */
    void save(T entity, Duration ttl, ConsistencyLevel level, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

}
