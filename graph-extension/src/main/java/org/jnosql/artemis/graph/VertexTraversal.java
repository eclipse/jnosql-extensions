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

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The Graph Traversal that maps {@link org.apache.tinkerpop.gremlin.structure.Vertex}.
 * This Traversal is lazy, in other words, that just run after the
 */
public interface VertexTraversal {


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
     * @param predicate   the predicate condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when either key or predicate condition are null
     */
    VertexTraversal has(String propertyKey, P<?> predicate) throws NullPointerException;

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
     * Defines Vetex has not a property
     *
     * @param propertyKey the property key
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when propertyKey is null
     */
    VertexTraversal hasNot(String propertyKey) throws NullPointerException;

    /**
     * Defines an outbound relationship
     *
     * @param labels the labels in the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    VertexTraversal out(String... labels) throws NullPointerException;

    /**
     * Defines an inbound relationship
     *
     * @param labels the labels in the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    VertexTraversal in(String... labels) throws NullPointerException;

    /**
     * Defines both, either inbound or outbound, relationship
     *
     * @param labels the labels in the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    VertexTraversal both(String... labels) throws NullPointerException;

    /**
     * Defines a limit
     *
     * @param limit the limit in the traversal
     * @return a {@link VertexTraversal} with the limit
     */
    VertexTraversal limit(long limit);

    /**
     * Defines Vertex as label condition
     *
     * @param labels the labels in the condition
     * @return a {@link VertexTraversal} with the new condition
     * @throws NullPointerException when has any null element
     */
    VertexTraversal hasLabel(String... labels) throws NullPointerException;


    /**
     * Gets the first result
     *
     * @param <T> the entity type
     * @return the entity result otherwise {@link Optional#empty()}
     */
    <T> Optional<T> next();

    /**
     * Get the result as stream
     *
     * @param <T> the entity type
     * @return the entity result as {@link Stream}
     */
    <T> Stream<T> stream();

    /**
     * Get the result as stream
     *
     * @param <T>   the entity type
     * @param limit the limit to result
     * @return the entity result as {@link Stream}
     */
    <T> Stream<T> stream(int limit);
}
