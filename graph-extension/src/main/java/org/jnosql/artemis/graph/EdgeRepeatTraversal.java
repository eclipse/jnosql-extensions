/*
 *  Copyright (c) 2017 Otávio Santana and others
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
 *   Otavio Santana
 */
package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * The wrapper step to
 * {@link org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal#repeat(org.apache.tinkerpop.gremlin.process.traversal.Traversal)}
 * in the Edge type.
 */
public interface EdgeRepeatTraversal {


    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param value       the value to the condition
     * @return a {@link EdgeRepeatStepTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    EdgeRepeatStepTraversal has(String propertyKey, Object value) throws NullPointerException;

    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param predicate   the predicate condition
     * @return a {@link EdgeRepeatStepTraversal} with the new condition
     * @throws NullPointerException when either key or predicate condition are null
     */
    EdgeRepeatStepTraversal has(String propertyKey, P<?> predicate) throws NullPointerException;
    //
    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param value       the value to the condition
     * @return a {@link EdgeRepeatStepTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    default EdgeRepeatStepTraversal has(Supplier<String> propertyKey, Object value) throws NullPointerException {
        requireNonNull(propertyKey, "the supplier is required");
        return has(propertyKey.get(), value);
    }

    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param predicate   the predicate condition
     * @return a {@link EdgeRepeatStepTraversal} with the new condition
     * @throws NullPointerException when either key or predicate condition are null
     */
    default EdgeRepeatStepTraversal has(Supplier<String> propertyKey, P<?> predicate) throws NullPointerException{
        requireNonNull(propertyKey, "the supplier is required");
        return has(propertyKey.get(), predicate);
    }

    /**
     * Adds a equals condition to a query
     *
     * @param accessor the key
     * @param value    the value to the condition
     * @return a {@link EdgeRepeatStepTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    EdgeRepeatStepTraversal has(T accessor, Object value) throws NullPointerException;

    /**
     * Adds a equals condition to a query
     *
     * @param accessor  the key
     * @param predicate the predicate condition
     * @return a {@link EdgeRepeatStepTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    EdgeRepeatStepTraversal has(T accessor, P<?> predicate) throws NullPointerException;


    /**
     * Defines Vertex has not a property
     *
     * @param propertyKey the property key
     * @return a {@link EdgeRepeatStepTraversal} with the new condition
     * @throws NullPointerException when propertyKey is null
     */
    EdgeRepeatStepTraversal hasNot(String propertyKey) throws NullPointerException;

    /**
     * Defines Vertex has not a property
     *
     * @param propertyKey the property key
     * @return a {@link EdgeRepeatStepTraversal} with the new condition
     * @throws NullPointerException when propertyKey is null
     */
    default EdgeRepeatStepTraversal hasNot(Supplier<String> propertyKey) throws NullPointerException{
        requireNonNull(propertyKey, "the supplier is required");
        return hasNot(propertyKey.get());
    }
}
