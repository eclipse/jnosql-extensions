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
package org.jnosql.artemis.couchbase.document;


import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.search.SearchQuery;
import jakarta.nosql.mapping.document.DocumentTemplate;

import java.util.stream.Stream;

/**
 * A {@link DocumentTemplate} to couchbase
 */
public interface CouchbaseTemplate extends DocumentTemplate {


    /**
     * Executes the n1qlquery with params and then result que result
     *
     * @param n1qlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either n1qlQuery or params are null
     */
    <T> Stream<T> n1qlQuery(String n1qlQuery, JsonObject params);

    /**
     * Executes the n1qlquery  with params and then result que result
     *
     * @param n1qlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either n1qlQuery or params are null
     */
    <T> Stream<T> n1qlQuery(Statement n1qlQuery, JsonObject params);

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param n1qlQuery the query
     * @return the query result
     * @throws NullPointerException when either n1qlQuery or params are null
     */
    <T> Stream<T> n1qlQuery(String n1qlQuery);

    /**
     * Searches in Couchbase using Full Text Search
     *
     * @param <T>   the type
     * @param query the query to be used
     * @return the elements from the query
     * @throws NullPointerException when either the query or index are null
     */
    <T> Stream<T> search(SearchQuery query);

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param n1qlQuery the query
     * @return the query result
     * @throws NullPointerException when either n1qlQuery or params are null
     */
    <T> Stream<T> n1qlQuery(Statement n1qlQuery);
}
