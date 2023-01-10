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
package org.eclipse.jnosql.mapping.criteria.api;

/**
 * A predicate representing a restriction in a {@link CriteriaQuery}
 *
 * @param <T> The Entity type whose fetching is to be be restricted
 */
public interface Predicate<T> {

    /**
     * Create a negation of this restriction.
     *
     * @return not predicate
     */
    NegationPredicate<T> not();

    /**
     * Create a conjunction of this with the argument restriction
     *
     * @param restriction restriction
     * @return and predicate
     */
    ConjunctionPredicate<T> and(Predicate<T> restriction);

    /**
     * Create a disjunction of this with the argument restriction
     *
     * @param restriction restriction
     * @return or predicate
     */
    DisjunctionPredicate<T> or(Predicate<T> restriction);

}
