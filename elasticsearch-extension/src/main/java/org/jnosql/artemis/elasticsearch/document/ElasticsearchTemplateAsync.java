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
package org.jnosql.artemis.elasticsearch.document;


import org.elasticsearch.index.query.QueryBuilder;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.diana.api.ExecuteAsyncQueryException;

import java.util.List;
import java.util.function.Consumer;

/**
 * A {@link DocumentTemplateAsync} to elasticsearch
 */
public interface ElasticsearchTemplateAsync extends DocumentTemplateAsync {

    /**
     * Find entities from {@link QueryBuilder}
     *
     * @param query    the query
     * @param types    the type
     * @param callBack the callback
     * @throws NullPointerException when query is null
     */
    <T> void find(QueryBuilder query, Consumer<List<T>> callBack, String... types)
            throws NullPointerException, ExecuteAsyncQueryException;
}
