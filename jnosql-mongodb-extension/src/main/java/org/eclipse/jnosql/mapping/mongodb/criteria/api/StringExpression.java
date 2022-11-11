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
package org.eclipse.jnosql.mapping.mongodb.criteria.api;

/**
 * Type for query expressions representing a string Entity attribute
 *
 * @param <X> the root type
 * @param <Y> the entity type
 */
public interface StringExpression<X, Y> extends Expression<X, Y, String> {

    /**
     * Create a predicate for testing whether this expression satisfies the
     * given pattern
     *
     * @param pattern string
     * @return like predicate
     */
    Predicate<X> like(String pattern);

}
