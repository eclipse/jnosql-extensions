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


import org.jnosql.artemis.document.DocumentTemplate;

import java.util.List;
import java.util.Map;

/**
 * A {@link DocumentTemplate} to couchbase
 */
public interface ArangoDBTemplate extends DocumentTemplate {

    /**
     * Executes ArangoDB query language, AQL.
     * <p>FOR u IN users FILTER u.status == @status RETURN u </p>
     *
     * @param <T>    entity class
     * @param query  the query
     * @param values the named queries
     * @return the query result
     * @throws NullPointerException when either query or values are null
     */
    <T> List<T> aql(String query, Map<String, Object> values) throws NullPointerException;

}
