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
package org.eclipse.jnosql.mapping.mongodb.criteria;

import java.util.stream.Stream;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.EntityQueryResult;

/**
 * Default implementation for {@link EntityQueryResult}
 * This holds the results entity stream
 *
 * @param <T> the type of the root entity
 */
public class DefaultEntityQueryResult<T> implements EntityQueryResult<T> {
    
    private final Stream<T> results;

    public DefaultEntityQueryResult(Stream<T> results) {
        this.results = results;
    }

    @Override
    public Stream<T> getEntities() {
        return this.results;
    }
    
}
