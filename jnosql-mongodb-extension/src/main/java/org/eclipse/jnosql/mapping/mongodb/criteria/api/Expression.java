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

import org.eclipse.jnosql.mapping.metamodel.api.Attribute;
import java.util.Collection;

/**
 * Type for query expressions representing an Entity attribute
 *
 * @param <X> the root type
 * @param <Y> the entity type
 * @param <T> the type of the expression
 */
public interface Expression<X, Y, T> {

    /**
     * Retrieves the path of this expression
     *
     * @return path
     */    
    Path<X, Y> getPath();

    /**
     * Retrieves the attribute of this expression
     *
     * @return attribute
     */        
    Attribute<Y, T> getAttribute();

    /**
     * Create a predicate for testing if the expression is equal to the argument
     * expression
     *
     * @param expression the expression to check the equality against
     * @return equality predicate
     */
    BinaryPredicate<X, T, Expression<X, Y, T>> equal(Expression<X, Y, T> expression);

    /**
     * Create a predicate for testing if the expression is equal to the argument
     * value
     *
     * @param value the value to check the equality against
     * @return equality predicate
     */
    BinaryPredicate<X, T, T> equal(T value);

    /**
     * Create a predicate to test whether the expression is a member of the
     * argument list.
     *
     * @param values values to be tested against
     * @return predicate testing for membership
     */
    BinaryPredicate<X, T, Collection<T>> in(Collection<T> values);

}
