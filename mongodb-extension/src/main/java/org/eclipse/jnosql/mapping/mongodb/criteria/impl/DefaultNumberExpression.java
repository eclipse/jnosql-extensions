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
package org.eclipse.jnosql.mapping.mongodb.criteria.impl;

import org.eclipse.jnosql.mapping.mongodb.criteria.api.ExpressionFunction;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.NumberExpression;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.Path;
import org.eclipse.jnosql.mapping.mongodb.metamodel.api.NumberAttribute;

/**
 * Default implementation for {@link NumberExpression}
 *
 * @param <X> the root type
 * @param <Y> the entity type
 * @param <N> the number type of the expression
 */
public class DefaultNumberExpression<X, Y, N extends Number & Comparable> extends DefaultComparableExpression<X, Y, N> implements NumberExpression<X, Y, N> {

    public DefaultNumberExpression(Path<X, Y> path, NumberAttribute attribute) {
        super(path, attribute);
    }
    
    @Override
    public ExpressionFunction<X, Y, N, N> sum() {
        return new DefaultExpressionFunction<>(this, ExpressionFunction.Function.SUM);
    }
    
}
