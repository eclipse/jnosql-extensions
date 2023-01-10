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
package org.eclipse.jnosql.mapping.criteria.api;

import jakarta.nosql.Value;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * The <code>FunctionQuery</code> interface defines functionality that is
 * specific to function queries.
 *
 * @param <X> the type of the root entity
 */
public interface FunctionQuery<X> extends RestrictedQuery<X, FunctionQueryResult<X>, FunctionQuery<X>, Stream<List<Value>>> {
    
    /**
     * Return the collection of {@link CriteriaFunction}s to retrieve.
     *
     * @return collection of functions
     */
    Collection<CriteriaFunction<X, ?, ?, ?>> getFunctions();
    
    /**
     * Specify the expressions that are used to form groups over
     * the query results.
     * Replaces the previous specified grouping expressions, if any.
     * If no grouping expressions are specified, any previously 
     * added grouping expressions are simply removed.
     * @param grouping  zero or more grouping expressions
     * @return the aggregated query
     */
    AggregatedQuery<X> groupBy(Expression<X, ?, ?>... grouping);

}
