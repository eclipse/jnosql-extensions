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
package org.eclipse.jnosql.mapping.criteria.api;

/**
 * An object that defines an ordering over the query results
 *
 * @param <X> the root type
 * @param <T> the type of the defined result
 */
public interface Order<X, T extends Comparable> {

    /**
     * Whether ascending ordering is in effect
     *
     * @return boolean indicating whether ordering is ascending
     */
    boolean isAscending();

    /**
     * Return the expression that is used for ordering
     *
     * @return expression used for ordering
     */
    ComparableExpression<X, ?, T> getExpression();

}
