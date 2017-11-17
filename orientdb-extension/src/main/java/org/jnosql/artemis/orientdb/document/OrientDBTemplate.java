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
package org.jnosql.artemis.orientdb.document;


import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link DocumentTemplate} to orientdb
 */
public interface OrientDBTemplate extends DocumentTemplate {

    /**
     * Find using OrientDB native query
     *
     * @param query  the query
     * @param params the params
     * @return the query result
     * @throws NullPointerException when either query or params are null
     */
    <T> List<T> sql(String query, Object... params) throws NullPointerException;

    /**
     * Find using OrientDB native query with map params
     *
     * @param query  the query
     * @param params the params
     * @return the query result
     * @throws NullPointerException when either query or params are null
     */
    <T> List<T> sql(String query, Map<String, Object> params) throws NullPointerException;
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
