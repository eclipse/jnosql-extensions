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
import org.jnosql.artemis.column.ColumnTemplate;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.time.Duration;
import java.util.List;

/**
 * A Cassandra extension of {@link org.jnosql.artemis.column.ColumnTemplate}
 */
public interface CassandraTemplate extends ColumnTemplate {

    /**
     * Saves a ColumnEntity with a defined ConsistencyLevel
     *
     * @param <T>    type
     * @param entity the entity
     * @param level  the {@link ConsistencyLevel}
     * @return the entity saved
     * @throws NullPointerException when both entity or level are null
     */

    <T> T save(T entity, ConsistencyLevel level) throws NullPointerException;


    /**
     * Saves an entity using {@link ConsistencyLevel}
     *
     * @param <T>      type
     * @param entities the entities
     * @param ttl      the ttl
     * @param level    the level
     * @return the entity saved
     * @throws NullPointerException when either entity or ttl or level are null
     */
    <T> Iterable<T> save(Iterable<T> entities, Duration ttl, ConsistencyLevel level) throws NullPointerException;

    /**
     * Saves a ColumnEntity with a defined ConsistencyLevel
     *
     * @param <T>      type
     * @param entities the entities
     * @param level    the {@link ConsistencyLevel}
     * @return the entity saved
     * @throws NullPointerException when both entity or level are null
     */

    <T> Iterable<T> save(Iterable<T> entities, ConsistencyLevel level) throws NullPointerException;


    /**
     * Saves an entity using {@link ConsistencyLevel}
     *
     * @param <T>    type
     * @param entity the entity
     * @param ttl    the ttl
     * @param level  the level
     * @return the entity saved
     * @throws NullPointerException when either entity or ttl or level are null
     */
    <T> T save(T entity, Duration ttl, ConsistencyLevel level) throws NullPointerException;


    /**
     * Deletes an information using {@link ConsistencyLevel}
     *
     * @param query the query
     * @param level the level
     * @throws NullPointerException when either query or level are null
     */
    void delete(ColumnDeleteQuery query, ConsistencyLevel level) throws NullPointerException;

    /**
     * Finds using a consistency level
     *
     * @param <T>   type
     * @param query the query
     * @param level the consistency level
     * @return the query using a consistency level
     */
    <T> List<T> find(ColumnQuery query, ConsistencyLevel level) throws NullPointerException;

    /**
     * Executes CQL
     *
     * @param <T>   type
     * @param query the Cassndra query language
     * @return the result of this query
     * @throws NullPointerException when query is null
     */
    <T> List<T> cql(String query) throws NullPointerException;

    /**
     * Executes CQL
     *
     * @param <T>    type
     * @param query  the Cassandra query language
     * @param params the params
     * @return the result of this query
     * @throws NullPointerException when query is null
     */
    <T> List<T> cql(String query, Object... params) throws NullPointerException;

    /**
     * Executes a statement
     *
     * @param <T>       type
     * @param statement the statement
     * @return the result of this query
     * @throws NullPointerException when statement is null
     */
    <T> List<T> execute(Statement statement) throws NullPointerException;

}
