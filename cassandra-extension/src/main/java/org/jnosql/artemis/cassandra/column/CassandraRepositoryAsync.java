/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.cassandra.column;


import com.datastax.driver.core.ConsistencyLevel;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.diana.api.ExecuteAsyncQueryException;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * A Cassandra extension of {@link RepositoryAsync}
 */
public interface CassandraRepositoryAsync<T, ID> extends RepositoryAsync<T, ID> {

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
     * @throws ExecuteAsyncQueryException when there is async issue
     * @throws NullPointerException       when there are any element null
     */
    void save(T entity, Duration ttl, ConsistencyLevel level) throws ExecuteAsyncQueryException, NullPointerException;

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
     * @throws ExecuteAsyncQueryException when there is async issue
     * @throws NullPointerException       when there are any element null
     */
    void save(Iterable<T> entities, Duration ttl, ConsistencyLevel level) throws ExecuteAsyncQueryException, NullPointerException;

    /**
     * Save the entity with ConsistencyLevel
     *
     * @param callBack the callBack
     * @param entity   the entity
     * @param level    {@link ConsistencyLevel}
     * @throws ExecuteAsyncQueryException when there is async issue
     * @throws NullPointerException       when there are any element null
     */
    void save(T entity, ConsistencyLevel level, Consumer<T> callBack) throws ExecuteAsyncQueryException, NullPointerException;

    /**
     * Saves the entity with ConsistencyLevel
     *
     * @param callBack the callBack
     * @param entity   the entity
     * @param ttl      the ttl
     * @param level    {@link ConsistencyLevel}
     * @throws ExecuteAsyncQueryException when there is async issue
     * @throws NullPointerException       when there are any element null
     */
    void save(T entity, Duration ttl, ConsistencyLevel level, Consumer<T> callBack) throws ExecuteAsyncQueryException
            , NullPointerException;

}
