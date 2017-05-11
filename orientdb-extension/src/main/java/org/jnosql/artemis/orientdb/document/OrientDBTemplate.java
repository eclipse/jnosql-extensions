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
package org.jnosql.artemis.orientdb.document;


import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.List;
import java.util.function.Consumer;

/**
 * A {@link DocumentTemplate} to orientdb
 */
public interface OrientDBTemplate extends DocumentTemplate {

    /**
     * Find using query
     *
     * @param query  the query
     * @param params the params
     * @return the query result
     * @throws NullPointerException when either query or params are null
     */
    <T> List<T> select(String query, Object... params) throws NullPointerException;
    /**
     * Execute live query
     *
     * @param query    the query
     * @param callBack when a new callback is coming
     * @throws NullPointerException when both query and callBack are null
     */
    <T> void live(DocumentQuery query, Consumer<T> callBack) throws NullPointerException;

    /**
     * Execute live query
     *
     * @param query    the string query, you must add "live"
     * @param callBack when a new entity is coming
     * @param params   the params
     * @throws NullPointerException when both query, callBack are null
     */
    <T> void live(String query, Consumer<T> callBack, Object... params);
}
