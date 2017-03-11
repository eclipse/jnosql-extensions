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
package org.jnosql.artemis.couchbase.document;


import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Statement;
import org.jnosql.artemis.document.DocumentRepository;

import java.util.List;

/**
 * A {@link DocumentRepository} to couchbase
 */
public interface CouchbaseDocumentRepository extends DocumentRepository {


    /**
     * Executes the n1qlquery with params and then result que result
     *
     * @param n1qlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either n1qlQuery or params are null
     */
    <T> List<T> n1qlQuery(String n1qlQuery, JsonObject params) throws NullPointerException;

    /**
     * Executes the n1qlquery  with params and then result que result
     *
     * @param n1qlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either n1qlQuery or params are null
     */
    <T> List<T> n1qlQuery(Statement n1qlQuery, JsonObject params) throws NullPointerException;

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param n1qlQuery the query
     * @return the query result
     * @throws NullPointerException when either n1qlQuery or params are null
     */
    <T> List<T> n1qlQuery(String n1qlQuery) throws NullPointerException;

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param n1qlQuery the query
     * @return the query result
     * @throws NullPointerException when either n1qlQuery or params are null
     */
    <T> List<T> n1qlQuery(Statement n1qlQuery) throws NullPointerException;
}
