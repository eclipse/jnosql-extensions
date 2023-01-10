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

/**
 * Type for query expressions representing a number attribute
 *
 * @param <X> the root type
 * @param <Y> the entity type
 * @param <T> the number type of the expression
 */
public interface NumberExpression<X, Y, T extends Number & Comparable> extends ComparableExpression<X, Y, T> {

    /**
     * Return the {@link CriteriaFunction} to sum this {@link NumberExpression}.
     *
     * @return operator
     */
    ExpressionFunction<X, Y, T, T> sum();
    
}
