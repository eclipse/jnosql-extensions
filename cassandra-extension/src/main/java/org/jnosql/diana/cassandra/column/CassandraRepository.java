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

package org.jnosql.diana.cassandra.column;


import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Statement;
import org.jnosql.artemis.column.ColumnRepository;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.time.Duration;
import java.util.List;

/**
 * A Cassandra extension of {@link ColumnRepository}
 */
public interface CassandraRepository extends ColumnRepository {

    /**
     * Saves a ColumnEntity with a defined ConsistencyLevel
     *
     * @param entity the entity
     * @param level  the {@link ConsistencyLevel}
     * @return the entity saved
     * @throws NullPointerException when both entity or level are null
     */
    <T> T save(T entity, ConsistencyLevel level) throws NullPointerException;


    /**
     * Saves an entity using {@link ConsistencyLevel}
     *
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
     * @param query the query
     * @param level the consistency level
     * @return the query using a consistency level
     */
    <T> List<T> find(ColumnQuery query, ConsistencyLevel level)throws NullPointerException;;

    /**
     * Executes CQL
     *
     * @param query the Cassndra query language
     * @return the result of this query
     * @throws NullPointerException when query is null
     */
    <T> List<T> cql(String query) throws NullPointerException;
    /**
     * Executes a statement
     *
     * @param statement the statement
     * @return the result of this query
     * @throws NullPointerException when statement is null
     */
    <T> List<T> execute(Statement statement) throws NullPointerException;

}
