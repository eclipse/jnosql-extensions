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
 * An {@link Predicate} to apply a range operator
 *
 * @param <X> The root type
 * @param <L> The left hand side type
 * @param <R> The right hand side type
 */
public interface RangePredicate<X, L, R> extends Predicate<X> {
    
    enum Operator {
        INCLUSIVE_BETWEEN,
        EXCLUSIVE_BETWEEN
    }
    
    /**
     * Return the operator for this {@link Predicate}.
     *
     * @return negated predicate
     */
    Operator getOperator();

    /**
     * Return the left hand side for this {@link Predicate}.
     *
     * @return negated predicate
     */
    Expression<X, ?, L> getLeft();

    /**
     * Return the from value for this {@link Predicate}.
     *
     * @return negated predicate
     */
    R getFrom();
    
    /**
     * Return the to value for this {@link Predicate}.
     *
     * @return negated predicate
     */
    R getTo();

}
