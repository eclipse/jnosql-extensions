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
import com.datastax.driver.core.Statement;
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnQuery;
import jakarta.nosql.mapping.column.ColumnTemplateAsync;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A Cassandra extension of {@link jakarta.nosql.mapping.column.ColumnTemplateAsync}
 */
public interface CassandraTemplateAsync extends ColumnTemplateAsync {

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
     * @param <T>    the type
     * @param entity the entity
     * @param ttl    the ttl
     * @param level  {@link ConsistencyLevel}
     * @throws jakarta.nosql.ExecuteAsyncQueryException when there is async issue
     * @throws NullPointerException       when there are any element null
     */
    <T> void save(T entity, Duration ttl, ConsistencyLevel level);

    /**
     * Save the entity with ConsistencyLevel
     *
     * @param <T>      the type
     * @param entities the entities
     * @param level    {@link ConsistencyLevel}
     */
    <T> void save(Iterable<T> entities, ConsistencyLevel level);

    /**
     * Saves the entity with ConsistencyLevel
     *
     * @param <T>      the type
     * @param entities the entities
     * @param ttl      the ttl
     * @param level    {@link ConsistencyLevel}
     * @throws jakarta.nosql.ExecuteAsyncQueryException when there is async issue
     * @throws NullPointerException       when there are any element null
     */
    <T> void save(Iterable<T> entities, Duration ttl, ConsistencyLevel level);

    /**
     * Save the entity with ConsistencyLevel
     *
     * @param callBack the callBack
     * @param <T>      the type
     * @param entity   the entity
     * @param level    {@link ConsistencyLevel}
     */
    <T> void save(T entity, ConsistencyLevel level, Consumer<T> callBack);

    /**
     * Saves the entity with ConsistencyLevel
     *
     * @param <T>      the type
     * @param callBack the callBack
     * @param entity   the entity
     * @param ttl      the ttl
     * @param level    {@link ConsistencyLevel}
     * @throws jakarta.nosql.ExecuteAsyncQueryException when there is async issue
     * @throws NullPointerException when there are any element null
     */
    <T> void save(T entity, Duration ttl, ConsistencyLevel level, Consumer<T> callBack);


    /**
     * Deletes an entity with consistency level
     *
     * @param <T>   the type
     * @param query the query
     * @param level {@link ConsistencyLevel}
     * @throws NullPointerException when both query or level are null
     */
    <T> void delete(ColumnDeleteQuery query, ConsistencyLevel level);

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
     * @throws jakarta.nosql.ExecuteAsyncQueryException a thread exception
     * @throws NullPointerException       when any arguments are null
     */
    <T> void select(ColumnQuery query, ConsistencyLevel level, Consumer<Stream<T>> consumer);

    /**
     * Executes CQL
     *
     * @param <T>      the type
     * @param query    the query
     * @param consumer the callback
     * @throws jakarta.nosql.ExecuteAsyncQueryException a thread exception
     */
    <T> void cql(String query, Consumer<Stream<T>> consumer);

    /**
     * Executes CQL using the provided named values.
     * E.g.: "SELECT * FROM users WHERE id = :i", Map&#60;String, Object&#62;of("i", 1)"
     *
     * @param <T>      the type
     * @param query    the query
     * @param values   values required for the execution of {@code query}
     * @param consumer the callback
     * @throws jakarta.nosql.ExecuteAsyncQueryException a thread exception
     */
    <T> void cql(String query, Map<String, Object> values, Consumer<Stream<T>> consumer);

    /**
     * Executes CQL
     *
     * @param <T>    type
     * @param query  the Cassandra query language
     * @param params the params
     * @throws NullPointerException when query is null
     */
    <T> void cql(String query, Consumer<Stream<T>> consumer, Object... params);

    /**
     * Executes statement
     *
     * @param <T>       the type
     * @param statement the query
     * @param consumer  the callback
     * @throws jakarta.nosql.ExecuteAsyncQueryException a thread exception
     * @throws NullPointerException       when either statment and callback is null
     */
    <T> void execute(Statement statement, Consumer<Stream<T>> consumer);


}
