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
 * The base conditions to {@link VertexTraversal} and {@link VertexUntilTraversal}
 */
public interface VertexConditionTraversal {

    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param value       the value to the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    VertexTraversal has(String propertyKey, Object value) throws NullPointerException;

    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when the propertyKey is null
     */
    VertexTraversal has(String propertyKey) throws NullPointerException;


    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param predicate   the predicate condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when either key or predicate condition are null
     */
    VertexTraversal has(String propertyKey, P<?> predicate) throws NullPointerException;

    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param value       the value to the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    default VertexTraversal has(Supplier<String> propertyKey, Object value) throws NullPointerException{
        requireNonNull(propertyKey, "the supplier is required");
        return has(propertyKey.get(), value);
    }

    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param predicate   the predicate condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when either key or predicate condition are null
     */
    default VertexTraversal has(Supplier<String> propertyKey, P<?> predicate) throws NullPointerException{
        requireNonNull(propertyKey, "the supplier is required");
        return has(propertyKey.get(), predicate);
    }

    /**
     * Adds a equals condition to a query
     *
     * @param accessor the key
     * @param value    the value to the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    VertexTraversal has(T accessor, Object value) throws NullPointerException;

    /**
     * Adds a equals condition to a query
     *
     * @param accessor  the key
     * @param predicate the predicate condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    VertexTraversal has(T accessor, P<?> predicate) throws NullPointerException;


    /**
     * Defines Vertex has not a property
     *
     * @param propertyKey the property key
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when propertyKey is null
     */
    VertexTraversal hasNot(String propertyKey) throws NullPointerException;

    /**
     * Defines Vertex has not a property
     *
     * @param propertyKey the property key
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when propertyKey is null
     */
    default VertexTraversal hasNot(Supplier<String> propertyKey) throws NullPointerException{
        requireNonNull(propertyKey, "the supplier is required");
        return hasNot(propertyKey.get());
    }

    /**
     * Map the {@link VertexTraversal} to its outgoing adjacent vertices given the edge labels.
     *
     * @param labels the edge labels to traverse
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    VertexTraversal out(String... labels) throws NullPointerException;


    /**
     * Map the {@link VertexTraversal} to its adjacent vertices given the edge labels.
     *
     * @param labels the edge labels to traverse
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    VertexTraversal in(String... labels) throws NullPointerException;


    /**
     * Map the {@link VertexTraversal} to its incoming adjacent vertices given the edge labels.
     *
     * @param labels the edge labels to traverse
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    VertexTraversal both(String... labels) throws NullPointerException;

    /**
     * Map the {@link VertexTraversal} to its outgoing adjacent vertices given the edge labels.
     *
     * @param label the edge labels to traverse
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    default VertexTraversal out(Supplier<String> label) throws NullPointerException{
        requireNonNull(label, "the supplier is required");
        return out(label.get());
    }

    /**
     * Map the {@link VertexTraversal} to its adjacent vertices given the edge labels.
     *
     * @param label the edge labels to traverse
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    default VertexTraversal in(Supplier<String> label) throws NullPointerException{
        requireNonNull(label, "the supplier is required");
        return in(label.get());
    }


    /**
     * Map the {@link VertexTraversal} to its incoming adjacent vertices given the edge labels.
     *
     * @param label the edge labels to traverse
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    default VertexTraversal both(Supplier<String> label) throws NullPointerException{
        requireNonNull(label, "the supplier is required");
        return both(label.get());
    }

    /**
     * Defines Vertex as label condition
     *
     * @param label the labels in the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when label is null
     */
    VertexTraversal hasLabel(String label) throws NullPointerException;

    /**
     * Defines Vertex as label condition
     *
     * @param entityClass reads the {@link org.jnosql.artemis.Entity} annotation otherwise the {@link Class#getSimpleName()}
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when entityClazz is null
     */
    <T> VertexTraversal hasLabel(Class<T> entityClass) throws NullPointerException;

    /**
     * Defines Vertex as label condition
     *
     * @param label the labels in the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    default VertexTraversal hasLabel(Supplier<String> label) throws NullPointerException{
        requireNonNull(label, "the supplier is required");
        return hasNot(label.get());
    }



}
