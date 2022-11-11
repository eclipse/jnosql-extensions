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
 * A function to be applied to a path, to be retrieved in a function query
 *
 * @param <X> the root entity type
 * @param <Y> the entity type
 * @param <T> the type of the attribute the function is applied to
 * @param <R> the return type of the function
 */
public interface PathFunction<X, Y, T, R> extends CriteriaFunction<X, Y, T, R> {

    /**
     * Supported path functions
     *
     */
    enum Function {
        COUNT
    }

    /**
     * Retrieves the path the function must be applied to
     *
     * @return the path
     */
    Path<X, Y> getPath();

    /**
     * Retrieves the function to apply to the path
     *
     * @return attribute
     */
    Function getFunction();

}
