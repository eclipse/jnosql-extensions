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
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The Graph Traversal that maps {@link org.apache.tinkerpop.gremlin.structure.Edge}.
 * This Traversal is lazy, in other words, that just run after the
 */
public interface EdgeTraversal {


    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param value       the value to the condition
     * @return a {@link EdgeTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    EdgeTraversal has(String propertyKey, Object value) throws NullPointerException;

    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param predicate   the predicate condition
     * @return a {@link EdgeTraversal} with the new condition
     * @throws NullPointerException when either key or predicate condition are null
     */
    EdgeTraversal has(String propertyKey, P<?> predicate) throws NullPointerException;
    //
    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param value       the value to the condition
     * @return a {@link EdgeTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    default EdgeTraversal has(Supplier<String> propertyKey, Object value) throws NullPointerException {
        requireNonNull(propertyKey, "the supplier is required");
        return has(propertyKey.get(), value);
    }

    /**
     * Adds a equals condition to a query
     *
     * @param propertyKey the key
     * @param predicate   the predicate condition
     * @return a {@link EdgeTraversal} with the new condition
     * @throws NullPointerException when either key or predicate condition are null
     */
    default EdgeTraversal has(Supplier<String> propertyKey, P<?> predicate) throws NullPointerException{
        requireNonNull(propertyKey, "the supplier is required");
        return has(propertyKey.get(), predicate);
    }

    /**
     * Adds a equals condition to a query
     *
     * @param accessor the key
     * @param value    the value to the condition
     * @return a {@link EdgeTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    EdgeTraversal has(T accessor, Object value) throws NullPointerException;

    /**
     * Adds a equals condition to a query
     *
     * @param accessor  the key
     * @param predicate the predicate condition
     * @return a {@link EdgeTraversal} with the new condition
     * @throws NullPointerException when either key or value are null
     */
    EdgeTraversal has(T accessor, P<?> predicate) throws NullPointerException;


    /**
     * Defines Vertex has not a property
     *
     * @param propertyKey the property key
     * @return a {@link EdgeTraversal} with the new condition
     * @throws NullPointerException when propertyKey is null
     */
    EdgeTraversal hasNot(String propertyKey) throws NullPointerException;

    /**
     * Defines Vertex has not a property
     *
     * @param propertyKey the property key
     * @return a {@link EdgeTraversal} with the new condition
     * @throws NullPointerException when propertyKey is null
     */
    default EdgeTraversal hasNot(Supplier<String> propertyKey) throws NullPointerException{
        requireNonNull(propertyKey, "the supplier is required");
        return hasNot(propertyKey.get());
    }


    /**
     * Filter the objects in the traversal by the number of them to pass through the next, where only the first
     * {@code n} objects are allowed as defined by the {@code limit} argument.
     * @param limit the number at which to end the next
     * @return a {@link EdgeTraversal} with the limit
     */
    EdgeTraversal limit(long limit);


    /**
     * Starts the loop traversal graph
     * @return a {@link EdgeRepeatTraversal}
     */
    EdgeRepeatTraversal repeat();


    /**
     * Returns the next elements in the traversal.
     * If the traversal is empty, then an {@link Optional#empty()} is returned.
     *
     * @param <IN>  inbound
     * @param <OUT> outbound
     * @return the EdgeEntity result otherwise {@link Optional#empty()}
     */
    <OUT, IN> Optional<EdgeEntity<OUT, IN>> next();


    /**
     * Converts to vertex traversal taking the inbound Vertex
     *
     * @return {@link VertexTraversal}
     */
    VertexTraversal inV();

    /**
     * Converts to vertex traversal taking the outbound Vertex
     *
     * @return {@link VertexTraversal}
     */
    VertexTraversal outV();

    /**
     * Converts to vertex traversal taking both inbound and outbound Vertex
     *
     * @return {@link VertexTraversal}
     */
    VertexTraversal bothV();

    /**
     * Get all the result in the traversal as Stream
     *
     * @param <IN>  inbound
     * @param <OUT> outbound
     * @return the entity result as {@link Stream}
     */
    <OUT, IN> Stream<EdgeEntity<OUT, IN>> stream();

    /**
     * Get the next n elements result as next, the number of elements is limit based
     *
     * @param <IN>  inbound
     * @param <OUT> outbound
     * @param limit the limit to result
     * @return the entity result as {@link Stream}
     */
    <OUT, IN> Stream<EdgeEntity<OUT, IN>> next(int limit);

    /**
     * Map the {@link org.apache.tinkerpop.gremlin.structure.Element} to a {@link java.util.Map} of the properties key'd according
     * to their {@link org.apache.tinkerpop.gremlin.structure.Property#key}.
     * If no property keys are provided, then all properties are retrieved.
     *
     * @param propertyKeys the properties to retrieve
     * @return a {@link ValueMapTraversal} instance
     */
    ValueMapTraversal valueMap(final String... propertyKeys);


    /**
     * Map the traversal next to its reduction as a sum of the elements
     *
     * @return the sum
     */
    long count();
}
