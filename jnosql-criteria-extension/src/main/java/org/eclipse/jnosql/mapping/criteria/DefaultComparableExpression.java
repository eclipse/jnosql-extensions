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
package org.eclipse.jnosql.mapping.criteria;

import org.eclipse.jnosql.mapping.criteria.api.BinaryPredicate;
import org.eclipse.jnosql.mapping.criteria.api.ComparableExpression;
import org.eclipse.jnosql.mapping.criteria.api.Expression;
import org.eclipse.jnosql.mapping.criteria.api.Order;
import org.eclipse.jnosql.mapping.criteria.api.Path;
import org.eclipse.jnosql.mapping.criteria.api.RangePredicate;
import org.eclipse.jnosql.mapping.metamodel.api.ComparableAttribute;

/**
 * Default implementation for {@link ComparableExpression}
 *
 * @param <X> the root type
 * @param <Y> the entity type
 * @param <T> the comparable type of the expression
 */
public class DefaultComparableExpression<X, Y, T extends Comparable> extends DefaultExpression<X, Y, T> implements ComparableExpression<X, Y, T> {

    public DefaultComparableExpression(Path<X, Y> path, ComparableAttribute<Y, T> attribute) {
        super(path, attribute);
    }
    
    @Override
    public BinaryPredicate<X, T, Expression<X, ?, T>> greaterThan(Expression<X, ?, T> expression) {
        return new DefaultBinaryPredicate(BinaryPredicate.Operator.GREATER_THAN, this, expression);
    }

    @Override
    public BinaryPredicate<X, T, T> greaterThan(T y) {
        return new DefaultBinaryPredicate(BinaryPredicate.Operator.GREATER_THAN, this, y);
    }

    @Override
    public BinaryPredicate<X, T, Expression<X, ?, T>> greaterThanOrEqualTo(Expression<X, ?, T> expression) {
        return new DefaultBinaryPredicate(BinaryPredicate.Operator.GREATER_THAN_OR_EQUAL, this, expression);
    }

    @Override
    public BinaryPredicate<X, T, T> greaterThanOrEqualTo(T y) {
        return new DefaultBinaryPredicate(BinaryPredicate.Operator.GREATER_THAN_OR_EQUAL, this, y);
    }

    @Override
    public BinaryPredicate<X, T, Expression<X, ?, T>> lessThan(Expression<X, ?, T> expression) {
        return new DefaultBinaryPredicate(BinaryPredicate.Operator.LESS_THAN, this, expression);
    }

    @Override
    public BinaryPredicate<X, T, T> lessThan(T y) {
        return new DefaultBinaryPredicate(BinaryPredicate.Operator.LESS_THAN, this, y);
    }

    @Override
    public BinaryPredicate<X, T, Expression<X, ?, T>> lessThanOrEqualTo(Expression<X, ?, T> expression) {
        return new DefaultBinaryPredicate(BinaryPredicate.Operator.LESS_THAN_OR_EQUAL, this, expression);
    }

    @Override
    public BinaryPredicate<X, T, T> lessThanOrEqualTo(T y) {
        return new DefaultBinaryPredicate(BinaryPredicate.Operator.LESS_THAN_OR_EQUAL, this, y);
    }

    @Override
    public RangePredicate<X, T, Expression<X, ?, T>> exclusiveBetween(Expression<X, ?, T> from, Expression<X, ?, T> to) {
        return new DefaultRangePredicate(RangePredicate.Operator.EXCLUSIVE_BETWEEN, this, from, to);
    }

    @Override
    public RangePredicate<X, T, T> exclusiveBetween(T from, T to) {
        return new DefaultRangePredicate(RangePredicate.Operator.EXCLUSIVE_BETWEEN, this, from, to);
    }

    @Override
    public RangePredicate<X, T, Expression<X, ?, T>> inclusiveBetween(Expression<X, ?, T> from, Expression<X, ? , T> to) {
        return new DefaultRangePredicate(RangePredicate.Operator.INCLUSIVE_BETWEEN, this, from, to);
    }

    @Override
    public RangePredicate<X, T, T> inclusiveBetween(T from, T to) {
        return new DefaultRangePredicate(RangePredicate.Operator.INCLUSIVE_BETWEEN, this, from, to);
    }

    @Override
    public Order<X, T> asc() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Order<X, T> desc() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
