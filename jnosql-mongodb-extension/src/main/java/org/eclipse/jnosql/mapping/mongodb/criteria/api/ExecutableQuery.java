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

/**
 * The <code>ExecutableQuery</code> interface defines functionality that is
 * specific to executable queries.
 *
 * @param <T> the type of the root entity
 * @param <R> the type of the query result
 * @param <Q> the type of the actual query
 * @param <F> the type of the actual result data to feed
 */
public interface ExecutableQuery<T, R extends CriteriaQueryResult<T>, Q extends ExecutableQuery<T, R, Q, F>, F> {
    
    /**
     * Retrieves the root type. 
     *
     * @return root type
     */
    Class<T> getType();
    
    /**
     * Retrieves the result. 
     *
     * @return result
     */
    R getResult();
    
    /**
     * Feed the results specific for this kind of query.
     *
     * @param results the result
     * @return the same query instance
     * @throws NullPointerException if the argument is null
     */
    Q feed(F results);
    
}
