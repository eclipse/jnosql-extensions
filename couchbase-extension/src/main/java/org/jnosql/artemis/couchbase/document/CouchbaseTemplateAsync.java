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
package org.jnosql.artemis.couchbase.document;


import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Statement;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.diana.api.ExecuteAsyncQueryException;

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
     * @throws ExecuteAsyncQueryException an async error
     */
    <T> void n1qlQuery(String n1qlQuery, JsonObject params, Consumer<List<T>> callback) throws NullPointerException
            , ExecuteAsyncQueryException;

    /**
     * Executes the n1qlquery  with params and then result que result
     *
     * @param n1qlQuery the query
     * @param params    the params
     * @param callback  the callback
     * @throws NullPointerException       when either n1qlQuery or params are null
     * @throws ExecuteAsyncQueryException an async error
     */
    <T> void n1qlQuery(Statement n1qlQuery, JsonObject params, Consumer<List<T>> callback) throws NullPointerException,
            ExecuteAsyncQueryException;

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param n1qlQuery the query
     * @param callback  the callback
     * @throws NullPointerException       when either n1qlQuery or params are null
     * @throws ExecuteAsyncQueryException an async error
     */
    <T> void n1qlQuery(String n1qlQuery, Consumer<List<T>> callback) throws NullPointerException,
            ExecuteAsyncQueryException;

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param n1qlQuery the query
     * @param callback  the callback
     * @throws NullPointerException       when either n1qlQuery or params are null
     * @throws ExecuteAsyncQueryException an async error
     */
    <T> void n1qlQuery(Statement n1qlQuery, Consumer<List<T>> callback) throws
            NullPointerException, ExecuteAsyncQueryException;
}
