/*
 *  Copyright (c) 2022 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.mongodb.criteria.api;

import java.util.Collection;

/**
 * The <code>RestrictedQuery</code> interface defines functionality that is
 * specific to restricted queries.
 *
 * @param <T> the type of the root entity
 * @param <R> the type of the query result
 * @param <Q> the type of the restricted query
 * @param <F> the type of data to feed the query with
 */
public interface RestrictedQuery<T, R extends CriteriaQueryResult<T>, Q extends RestrictedQuery<T, R, Q, F>, F> extends ExecutableQuery<T, R, Q, F> {

    /**
     * Modify the query to restrict the query result according to the
     * conjunction of the specified restriction predicates. Replaces the
     * previously added restriction(s), if any.
     *
     * @param restrictions zero or more restriction predicates
     * @return the modified query
     */
    Q where(Predicate<T>... restrictions);

    /**
     * Retrieves the restriction collection. 
     *
     * @return the restrictions
     */
    Collection<Predicate<T>> getRestrictions();

}
