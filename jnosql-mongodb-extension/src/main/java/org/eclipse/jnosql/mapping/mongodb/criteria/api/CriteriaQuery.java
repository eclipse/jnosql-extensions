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
package org.eclipse.jnosql.mapping.mongodb.criteria.api;

/**
 * The <code>CriteriaQuery</code> interface defines functionality that is
 * specific to top-level queries.
 *
 * @param <T> the type of the root entity
 */
public interface CriteriaQuery<T> {

    /**
     * Returns the query root
     *
     * @return from clause
     */
    Root<T> from();

    /**
     * Creates a select query
     *
     * @return select query
     */    
    EntityQuery<T> select();
    
    /**
     * Creates a select function query
     *
     * @param functions to be computed
     * @return function query
     */    
    FunctionQuery<T> select(CriteriaFunction<T, ?, ?, ?>... functions);

    /**
     * Creates a select expression query
     *
     * @param expressions to retrieve
     * @return select query
     */    
    ExpressionQuery<T> select(Expression<T, ?, ?>... expressions);
    
}
