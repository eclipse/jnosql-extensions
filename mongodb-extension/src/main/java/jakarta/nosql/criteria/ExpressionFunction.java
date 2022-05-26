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
package jakarta.nosql.criteria;

/**
 * A function applied to an expression, to be retrieved in a function query
 *
 * @param <X> the root entity type
 * @param <Y> the entity type
 * @param <T> the type of the attribute the function is applied to
 * @param <R> the return type of the function
 */
public interface ExpressionFunction<X, Y, T, R> extends CriteriaFunction<X, Y, T, R> {

    /**
     * Supported expression functions
     *
     */
    enum Function {
        SUM
    }

    /**
     * Retrieves the expression the function must be applied to
     *
     * @return the expression
     */
    Expression<X, Y, T> getExpression();

    /**
     * Retrieves the function to apply to the expression
     *
     * @return attribute
     */
    Function getFunction();

}
