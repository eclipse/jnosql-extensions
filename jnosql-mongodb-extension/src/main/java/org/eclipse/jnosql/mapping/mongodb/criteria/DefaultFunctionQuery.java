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
import org.eclipse.jnosql.mapping.mongodb.criteria.api.AggregatedQuery;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.CriteriaFunction;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.Expression;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.FunctionQuery;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.FunctionQueryResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Default implementation for {@link FunctionQuery}
 * This holds the functions to retrieve.
 *
 * @param <T> the type of the expression
 */
public class DefaultFunctionQuery<T> extends AbstractRestrictedQuery<T, FunctionQueryResult<T>, DefaultFunctionQuery<T>> implements FunctionQuery<T> {

    private final Collection<CriteriaFunction<T, ?, ?, ?>> functions;

    public DefaultFunctionQuery(Class<T> type, CriteriaFunction<T, ?, ?, ?>... functions) {
        super(type);
        this.functions = Arrays.asList(functions);
    }

    @Override
    public Collection<CriteriaFunction<T, ?, ?, ?>> getFunctions() {
        return functions;
    }

    @Override
    public AggregatedQuery<T> groupBy(Expression<T, ?, ?>... groupings) {
        return new DefaultAggregatedQuery(this.getType(), groupings);
    }

    @Override
    public FunctionQueryResult<T> getResult() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FunctionQuery<T> feed(Stream<List<Value>> results) {
        return this;
    }

}
