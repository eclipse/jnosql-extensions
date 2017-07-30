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
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of {@link VertexTraversal}
 */
class DefaultVertexTraversal implements VertexTraversal {


    private final Supplier<GraphTraversal<Vertex, Vertex>> supplier;
    private final Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Vertex>> flow;
    private final VertexConverter converter;

    DefaultVertexTraversal(Supplier<GraphTraversal<Vertex, Vertex>> supplier,
                           Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Vertex>> flow,
                           VertexConverter converter) {
        this.supplier = supplier;
        this.flow = flow;
        this.converter = converter;
    }


    @Override
    public VertexTraversal has(String propertyKey, Object value) throws NullPointerException {
        requireNonNull(propertyKey, "propertyKey is required");
        requireNonNull(value, "value is required");

        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.has(propertyKey, value)), converter);
    }

    @Override
    public VertexTraversal has(String propertyKey, P<?> predicate) throws NullPointerException {
        requireNonNull(propertyKey, "propertyKey is required");
        requireNonNull(predicate, "predicate is required");
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.has(propertyKey, predicate)), converter);
    }

    @Override
    public VertexTraversal has(T accessor, Object value) throws NullPointerException {
        requireNonNull(accessor, "accessor is required");
        requireNonNull(value, "value is required");
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.has(accessor, value)), converter);
    }

    @Override
    public VertexTraversal has(T accessor, P<?> predicate) throws NullPointerException {
        requireNonNull(accessor, "accessor is required");
        requireNonNull(predicate, "predicate is required");
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.has(accessor, predicate)), converter);
    }

    @Override
    public VertexTraversal out(String... labels) throws NullPointerException {
        if (Stream.of(labels).anyMatch(Objects::isNull)) {
            throw new NullPointerException("The no one label element cannot be null");
        }
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.out(labels)), converter);
    }

    @Override
    public EdgeTraversal outE(String... edgeLabels) throws NullPointerException {
        if (Stream.of(edgeLabels).anyMatch(Objects::isNull)) {
            throw new NullPointerException("The no one edgeLabels element cannot be null");
        }
        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.outE(edgeLabels)), converter);
    }

    @Override
    public VertexTraversal in(String... labels) throws NullPointerException {
        if (Stream.of(labels).anyMatch(Objects::isNull)) {
            throw new NullPointerException("The no one label element cannot be null");
        }
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.in(labels)), converter);
    }

    @Override
    public EdgeTraversal inE(String... edgeLabels) throws NullPointerException {
        if (Stream.of(edgeLabels).anyMatch(Objects::isNull)) {
            throw new NullPointerException("The no one edgeLabels element cannot be null");
        }

        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.inE(edgeLabels)), converter);
    }

    @Override
    public VertexTraversal both(String... labels) throws NullPointerException {
        if (Stream.of(labels).anyMatch(Objects::isNull)) {
            throw new NullPointerException("The no one label element cannot be null");
        }
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.both(labels)), converter);
    }

    @Override
    public EdgeTraversal bothE(String... edgeLabels) throws NullPointerException {
        if (Stream.of(edgeLabels).anyMatch(Objects::isNull)) {
            throw new NullPointerException("The no one edgeLabels element cannot be null");
        }
        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.bothE(edgeLabels)), converter);
    }

    @Override
    public VertexTraversal limit(long limit) {
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.limit(limit)), converter);
    }


    @Override
    public VertexTraversal hasLabel(String... labels) throws NullPointerException {
        if (Stream.of(labels).anyMatch(Objects::isNull)) {
            throw new NullPointerException("The no one label element cannot be null");
        }
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.hasLabel(labels)), converter);
    }


    @Override
    public VertexTraversal hasNot(String propertyKey) throws NullPointerException {
        requireNonNull(propertyKey, "propertyKey is required");
        return new DefaultVertexTraversal(supplier, flow.andThen(g -> g.hasNot(propertyKey)), converter);
    }

    @Override
    public <T> Optional<T> next() {
        Optional<Vertex> vertex = flow.apply(supplier.get()).tryNext();
        if (vertex.isPresent()) {
            ArtemisVertex artemisVertex = TinkerPopUtil.toArtemisVertex(vertex.get());
            return Optional.of(converter.toEntity(artemisVertex));

        }
        return Optional.empty();
    }

    @Override
    public <T> Stream<T> stream() {
        return flow.apply(supplier.get()).toList().stream()
                .map(TinkerPopUtil::toArtemisVertex)
                .map(converter::toEntity);
    }

    @Override
    public <T> Stream<T> stream(int limit) {
        return flow.apply(supplier.get()).next(limit).stream()
                .map(TinkerPopUtil::toArtemisVertex)
                .map(converter::toEntity);
    }
}
