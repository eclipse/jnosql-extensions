/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
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
 *   Alessandro Moscatelli
 */
package org.eclipse.jnosql.mapping.criteria.api;

import jakarta.nosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.criteria.DefaultCriteriaQuery;

/**
 * A criteria extension of {@link DocumentTemplate}
 */
public interface CriteriaTemplate extends DocumentTemplate {
    
    /**
     * Create a <code>CriteriaQuery</code> object with the specified result
     * type.
     *
     * @param <T> type of the query result
     * @param type type of the query result
     * @return criteria query object
     * @throws NullPointerException query is null
     */
    default <T> CriteriaQuery<T> createQuery(Class<T> type) {
        return new DefaultCriteriaQuery<>(type);
    }

    /**
     * Executes a {@link CriteriaQuery}
     *
     * @param criteriaQuery - the query
     * @param <T> the instance type of the query
     * {@link org.eclipse.jnosql.mapping.criteria.api.Root}
     * @param <R> the result type of the query
     * @param <Q> the type of the actual query
     * @param <F> the type of data to feed the query with
     * @return query result
     * @throws NullPointerException when criteriaQuery is null
     */
    <T, R extends CriteriaQueryResult<T>, Q extends ExecutableQuery<T, R, Q, F>, F> R executeQuery(ExecutableQuery<T, R, Q, F> criteriaQuery);

}
