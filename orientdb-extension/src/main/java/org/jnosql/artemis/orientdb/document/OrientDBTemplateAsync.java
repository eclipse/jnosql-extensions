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


import org.jnosql.artemis.document.DocumentTemplateAsync;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link DocumentTemplateAsync} to orientdb
 */
public interface OrientDBTemplateAsync extends DocumentTemplateAsync {


    /**
     * Find using OrientDB native query with map params
     * @param query the query
     * @param callBack the callback
     * @param params the params
     * @param <T> the type
     */
    <T> void sql(String query, Consumer<List<T>> callBack, Object... params);

    /**
     * Find using OrientDB native query with map params
     * @param query the query
     * @param callBack the callback
     * @param params the params
     * @param <T> the type
     */
    <T> void sql(String query, Consumer<List<T>> callBack, Map<String, Object> params);
}
