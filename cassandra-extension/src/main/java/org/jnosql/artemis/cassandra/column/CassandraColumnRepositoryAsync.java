/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.cassandra.column;


import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Statement;
import org.jnosql.artemis.column.ColumnRepositoryAsync;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

/**
 * A Cassandra extension of {@link org.jnosql.artemis.column.ColumnRepositoryAsync}
 */
public interface CassandraColumnRepositoryAsync extends ColumnRepositoryAsync {

    /**
     * Save the entity with ConsistencyLevel
     *
     * @param <T>    the type
     * @param entity the entity
     * @param level  {@link ConsistencyLevel}
     */
    <T> void save(T entity, ConsistencyLevel level);


    /**
     * Save the entity with ConsistencyLevel
     *
     * @param callBack the callBack
     * @param <T>      the type
     * @param entity   the entity
     * @param level    {@link ConsistencyLevel}
     */
    <T> void save(T entity, ConsistencyLevel level, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Saves the entity with ConsistencyLevel
     *
     * @param <T>    the type
     * @param entity the entity
     * @param ttl    the ttl
     * @param level  {@link ConsistencyLevel}
     * @throws ExecuteAsyncQueryException
     * @throws UnsupportedOperationException
     */
    <T> void save(T entity, Duration ttl, ConsistencyLevel level) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;

    /**
     * Saves the entity with ConsistencyLevel
     *
     * @param <T>      the type
     * @param callBack the callBack
     * @param entity   the entity
     * @param ttl      the ttl
     * @param level    {@link ConsistencyLevel}
     * @throws ExecuteAsyncQueryException
     * @throws UnsupportedOperationException
     */
    <T> void save(T entity, Duration ttl, ConsistencyLevel level, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException;


    /**
     * Deletes an entity with consistency level
     *
     * @param <T>   the type
     * @param query the query
     * @param level {@link ConsistencyLevel}
     * @throws NullPointerException when both query or level are null
     */
    <T> void delete(ColumnDeleteQuery query, ConsistencyLevel level) throws NullPointerException;

    /**
     * Deletes an entity with consistencyLeel and consumter
     *
     * @param query    the query
     * @param consumer the callback
     * @param level    {@link ConsistencyLevel}
     */
    void delete(ColumnDeleteQuery query, ConsistencyLevel level, Consumer<Void> consumer);


    /**
     * Find async with ConsistencyLevel
     *
     * @param <T>      the type
     * @param query    the query
     * @param level    {@link ConsistencyLevel}
     * @param consumer the callBack
     * @throws ExecuteAsyncQueryException a thread exception
     * @throws NullPointerException       when any arguments are null
     */
    <T> void find(ColumnQuery query, ConsistencyLevel level, Consumer<List<T>> consumer)
            throws ExecuteAsyncQueryException, NullPointerException;

    /**
     * Executes CQL
     *
     * @param <T>      the type
     * @param query    the query
     * @param consumer the callback
     * @throws ExecuteAsyncQueryException a thread exception
     */
    <T> void cql(String query, Consumer<List<T>> consumer)
            throws ExecuteAsyncQueryException, NullPointerException;

    /**
     * Executes statement
     *
     * @param <T>       the type
     * @param statement the query
     * @param consumer  the callback
     * @throws ExecuteAsyncQueryException a thread exception
     * @throws NullPointerException       when either statment and callback is null
     */
    <T> void execute(Statement statement, Consumer<List<T>> consumer)
            throws ExecuteAsyncQueryException, NullPointerException;
}
