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
package org.jnosql.artemis.arangodb.document;


import jakarta.nosql.mapping.document.DocumentTemplateAsync;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A {@link DocumentTemplateAsync} to arangodb
 */
public interface ArangoDBTemplateAsync extends DocumentTemplateAsync {

    /**
     * Executes AQL, finds {@link jakarta.nosql.document.DocumentEntity} from select asynchronously
     * <p>FOR u IN users FILTER u.status == @status RETURN u </p>
     *
     * @param <T>      the entity type
     * @param query    the query
     * @param values   the named queries
     * @param callBack the callback, when the process is finished will call this instance returning
     *                 the result of select within parameters
     * @throws jakarta.nosql.ExecuteAsyncQueryException    when there is a async error
     * @throws UnsupportedOperationException when the database does not support this feature
     * @throws NullPointerException          when either select or callback are null
     */
    <T> void aql(String query, Map<String, Object> values, Consumer<Stream<T>> callBack);
}
