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
 * The <code>ExpressionQuery</code> interface defines functionality that is
 * specific to select queries retrieving only a subset of the entity fields.
 *
 * @param <X> the type of the root entity
 */
public interface ExpressionQuery<X> extends SelectQuery<X, ExpressionQueryResult<X>, ExpressionQuery<X>, Stream<List<Value>>> {

    /**
     * Retrieves the expressions of the fields to retrieve.
     *
     * @return the expressions
     */
    Collection<Expression<X, ?, ?>> getExpressions();

}
