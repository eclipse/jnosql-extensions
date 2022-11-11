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
package org.eclipse.jnosql.mapping.mongodb.criteria;

import jakarta.nosql.Value;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.Expression;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.ExpressionQuery;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default implementation for {@link ExpressionQuery} This holds the expressions
 * to retrieve.
 *
 * @param <X> the type of the expression
 */
public class DefaultExpressionQuery<X> extends DefaultSelectQuery<X, DefaultExpressionQueryResult<X>, DefaultExpressionQuery<X>> implements ExpressionQuery<X> {

    private final List<Expression<X, ?, ?>> expressions;

    public DefaultExpressionQuery(Class<X> type, Expression<X, ?, ?>... expressions) {
        super(type);
        this.expressions = Arrays.asList(expressions);
    }

    @Override
    public List<Expression<X, ?, ?>> getExpressions() {
        return expressions;
    }

    @Override
    public ExpressionQuery<X> feed(Stream<List<Value>> results) {
        this.setResult(
                new DefaultExpressionQueryResult(
                        results.map(
                                result -> new DefaultExpressionQueryResultRow(
                                        this.expressions.stream().collect(
                                                Collectors.toMap(
                                                        expression -> expression,
                                                        expression -> result.get(
                                                                this.expressions.indexOf(expression)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        return this;
    }

}
