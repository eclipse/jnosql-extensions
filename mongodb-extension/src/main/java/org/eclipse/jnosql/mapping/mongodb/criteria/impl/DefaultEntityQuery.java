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
package org.eclipse.jnosql.mapping.mongodb.criteria.impl;

import org.eclipse.jnosql.mapping.mongodb.criteria.api.EntityQuery;
import java.util.stream.Stream;

/**
 * Default implementation for {@link EntityQuery}
 * This holds the expressions to retrieve.
 *
 * @param <T> the type of the expression
 */
public class DefaultEntityQuery<T> extends DefaultSelectQuery<T, DefaultEntityQueryResult<T>, DefaultEntityQuery<T>> implements EntityQuery<T> {

    public DefaultEntityQuery(Class<T> type) {
        super(type);
    }

    @Override
    public EntityQuery<T> feed(Stream<T> results) {
        this.setResult(
                new DefaultEntityQueryResult(results)
        );
        return this;
    }
    
}
