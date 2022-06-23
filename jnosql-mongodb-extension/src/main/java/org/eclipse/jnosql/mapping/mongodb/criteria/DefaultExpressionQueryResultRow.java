/*
 * Copyright 2022 Eclipse Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.jnosql.mapping.mongodb.criteria;

import jakarta.nosql.Value;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.Expression;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.ExpressionQueryResultRow;
import java.util.Map;

/**
 * Default implementation for {@link ExpressionQueryResultRow}
 * This holds a map to retrieve strongly typed values based on key expression
 *
 * @param <X> the type of the root entity
 */
public class DefaultExpressionQueryResultRow<X> implements ExpressionQueryResultRow<X> {
    
    private final Map<Expression<X, ?, ?>, Value> map;
    
    public DefaultExpressionQueryResultRow(
            Map<Expression<X, ?, ?>, Value> map
    ) {
        this.map = map;
    }

    @Override
    public <T> T get(Expression<X, ?, T> expression) {
        return this.map.get(expression).get(
                expression.getAttribute().getAttributeType()
        );
    }
    
}
