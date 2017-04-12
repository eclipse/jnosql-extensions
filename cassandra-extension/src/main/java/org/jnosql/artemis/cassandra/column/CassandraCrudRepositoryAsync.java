/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
