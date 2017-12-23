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
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.jnosql.artemis.graph.util.TinkerPopUtil.toEdgeEntity;

class DefaultEdgeTraversal extends AbstractEdgeTraversal implements EdgeTraversal {


    DefaultEdgeTraversal(Supplier<GraphTraversal<?, ?>> supplier,
                         Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Edge>> flow,
                         VertexConverter converter) {
        super(supplier, flow, converter);
    }


    @Override
    public EdgeTraversal has(String propertyKey) throws NullPointerException {
        requireNonNull(propertyKey, "propertyKey is required");
        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.has(propertyKey)), converter);

    }

    @Override
    public EdgeTraversal has(String propertyKey, Object value) throws NullPointerException {
        requireNonNull(propertyKey, "propertyKey is required");
        requireNonNull(value, "value is required");

        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.has(propertyKey, value)), converter);
    }

    @Override
    public EdgeTraversal has(String propertyKey, P<?> predicate) throws NullPointerException {
        requireNonNull(propertyKey, "propertyKey is required");
        requireNonNull(predicate, "predicate is required");
        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.has(propertyKey, predicate)), converter);
    }

    @Override
    public EdgeTraversal has(T accessor, Object value) throws NullPointerException {
        requireNonNull(accessor, "accessor is required");
        requireNonNull(value, "value is required");
        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.has(accessor, value)), converter);
    }

    @Override
    public EdgeTraversal has(T accessor, P<?> predicate) throws NullPointerException {
        requireNonNull(accessor, "accessor is required");
        requireNonNull(predicate, "predicate is required");
        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.has(accessor, predicate)), converter);
    }

    @Override
    public EdgeTraversal hasNot(String propertyKey) throws NullPointerException {
        requireNonNull(propertyKey, "propertyKey is required");
        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.hasNot(propertyKey)), converter);
    }

    @Override
    public EdgeTraversal limit(long limit) {
        return new DefaultEdgeTraversal(supplier, flow.andThen(g -> g.limit(limit)), converter);
    }

    @Override
    public EdgeRepeatTraversal repeat() {
        return new DefaultEdgeRepeatTraversal(supplier, flow, converter);
    }


    @Override
    public VertexTraversal inV() {
        return new DefaultVertexTraversal(supplier, flow.andThen(GraphTraversal::inV), converter);
    }

    @Override
    public VertexTraversal outV() {
        return new DefaultVertexTraversal(supplier, flow.andThen(GraphTraversal::outV), converter);
    }

    @Override
    public VertexTraversal bothV() {
        return new DefaultVertexTraversal(supplier, flow.andThen(GraphTraversal::bothV), converter);
    }


    @Override
    public <OUT, IN> Optional<EdgeEntity<OUT, IN>> next() {
        Optional<Edge> edgeOptional = flow.apply(supplier.get()).tryNext();
        if (edgeOptional.isPresent()) {
            Edge edge = edgeOptional.get();
            return Optional.of(toEdge(edge));

        }
        return Optional.empty();
    }

    @Override
    public <OUT, IN> Stream<EdgeEntity<OUT, IN>> stream() {
        return flow.apply(supplier.get()).toList().stream().map(this::toEdge);
    }

    @Override
    public <OUT, IN> Stream<EdgeEntity<OUT, IN>> next(int limit) {
        return flow.apply(supplier.get()).next(limit).stream().map(this::toEdge);
    }

    @Override
    public ValueMapTraversal valueMap(String... propertyKeys) {
        return new DefaultValueMapTraversal(supplier, flow.andThen(g -> g.valueMap(propertyKeys)));
    }

    @Override
    public long count() {
        return flow.apply(supplier.get()).count().tryNext().orElse(0L);
    }


    <OUT, IN> EdgeEntity<OUT, IN> toEdge(Edge edge) {
        return toEdgeEntity(edge, converter);
    }


}
