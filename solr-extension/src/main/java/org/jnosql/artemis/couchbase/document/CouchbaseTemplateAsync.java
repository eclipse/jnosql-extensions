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
import jakarta.nosql.mapping.document.DocumentTemplateAsync;

import java.util.List;
import java.util.function.Consumer;

/**
 * A {@link DocumentTemplateAsync} to couchbase
 */
public interface CouchbaseTemplateAsync extends DocumentTemplateAsync {


    /**
     * Executes the n1qlquery with params and then result que result
     *
     * @param n1qlQuery the query
     * @param params    the params
     * @param callback  the callback
     * @throws NullPointerException       when either n1qlQuery or params are null
     * @throws jakarta.nosql.ExecuteAsyncQueryException an async error
     */
    <T> void n1qlQuery(String n1qlQuery, JsonObject params, Consumer<List<T>> callback);

    /**
     * Executes the n1qlquery  with params and then result que result
     *
     * @param n1qlQuery the query
     * @param params    the params
     * @param callback  the callback
     * @throws NullPointerException       when either n1qlQuery or params are null
     * @throws jakarta.nosql.ExecuteAsyncQueryException an async error
     */
    <T> void n1qlQuery(Statement n1qlQuery, JsonObject params, Consumer<List<T>> callback);

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param n1qlQuery the query
     * @param callback  the callback
     * @throws NullPointerException       when either n1qlQuery or params are null
     * @throws jakarta.nosql.ExecuteAsyncQueryException an async error
     */
    <T> void n1qlQuery(String n1qlQuery, Consumer<List<T>> callback);

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param n1qlQuery the query
     * @param callback  the callback
     * @throws NullPointerException       when either n1qlQuery or params are null
     * @throws jakarta.nosql.ExecuteAsyncQueryException an async error
     */
    <T> void n1qlQuery(Statement n1qlQuery, Consumer<List<T>> callback);
}
