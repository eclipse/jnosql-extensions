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
package org.eclipse.jnosql.mapping.criteria;

import org.eclipse.jnosql.mapping.criteria.api.EntityQueryResult;
import org.eclipse.jnosql.mapping.criteria.api.ExpressionQueryResult;
import org.eclipse.jnosql.mapping.criteria.api.ExpressionQueryResultRow;
import java.util.stream.Stream;

/**
 * Default implementation for {@link EntityQueryResult}
 * This contains the results {@link ExpressionQueryResultRow} stream
 *
 * @param <X> the type of the root entity
 */
public class DefaultExpressionQueryResult<X> implements ExpressionQueryResult<X> {
    
    private final Stream<ExpressionQueryResultRow<X>> rows;

    public DefaultExpressionQueryResult(Stream<ExpressionQueryResultRow<X>> rows) {
        this.rows = rows;
    }

    @Override
    public Stream<ExpressionQueryResultRow<X>> getRows() {
        return this.rows;
    }
    
}
