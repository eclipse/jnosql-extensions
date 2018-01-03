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

import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.EntityNotFoundException;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Value;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.tinkerpop.gremlin.structure.T.id;
import static org.jnosql.artemis.graph.util.TinkerPopUtil.toArtemisVertex;
import static org.jnosql.artemis.graph.util.TinkerPopUtil.toEdgeEntity;
import static org.jnosql.artemis.graph.util.TinkerPopUtil.toVertex;

public abstract class AbstractGraphTemplate implements GraphTemplate {
    private static final Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Vertex>> INITIAL_VERTEX =
            g -> (GraphTraversal<Vertex, Vertex>) g;

    private static final Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Edge>> INITIAL_EDGE =
            g -> (GraphTraversal<Vertex, Edge>) g;


    protected abstract Graph getGraph();

    protected abstract ClassRepresentations getClassRepresentations();

    protected abstract VertexConverter getVertex();

    protected abstract GraphWorkflow getFlow();

    @Override
    public <T> T insert(T entity) throws NullPointerException, IdNotFoundException {
        requireNonNull(entity, "entity is required");
        checkId(entity);

        UnaryOperator<ArtemisVertex> save = e -> {
            ArtemisVertex artemisVertex = getVertex().toVertex(entity);
            Vertex vertex = toVertex(artemisVertex, getGraph());
            return toArtemisVertex(vertex);
        };

        return getFlow().flow(entity, save);
    }

    @Override
    public <T> T update(T entity) throws NullPointerException, IdNotFoundException {
        requireNonNull(entity, "entity is required");
        checkId(entity);

        UnaryOperator<ArtemisVertex> update = e -> {
            ArtemisVertex artemisVertex = getVertex().toVertex(entity);
            Object idValue = artemisVertex.getId()
                    .map(Value::get)
                    .orElseThrow(() -> new NullPointerException("Id field is required"));

            Vertex vertex = getGraph()
                    .traversal()
                    .V(idValue)
                    .tryNext()
                    .orElseThrow(() -> new EntityNotFoundException(format("The entity %s with id %s is not found to update",
                            entity.getClass().getName(), idValue.toString())));

            artemisVertex.getProperties().forEach(p -> vertex.property(p.getKey(), p.get()));
            return artemisVertex;
        };
        return getFlow().flow(entity, update);
    }

    @Override
    public <T> void delete(T idValue) throws NullPointerException {
        requireNonNull(idValue, "id is required");
        List<Vertex> vertices = getGraph().traversal().V(idValue).toList();
        vertices.forEach(Vertex::remove);

    }

    @Override
    public <T> void deleteEdge(T idEdge) throws NullPointerException {
        requireNonNull(idEdge, "idEdge is required");
        List<Edge> edges = getGraph().traversal().E(idEdge).toList();
        edges.forEach(Edge::remove);
    }

    @Override
    public <T, ID> Optional<T> find(ID idValue) throws NullPointerException {
        requireNonNull(idValue, "id is required");
        Optional<Vertex> vertex = getGraph().traversal().V(idValue).tryNext();
        return vertex.map(vertex1 -> getVertex().toEntity(toArtemisVertex(vertex1)));
    }

    @Override
    public <OUT, IN> EdgeEntity<OUT, IN> edge(OUT outbound, String label, IN incoming) throws NullPointerException,
            IdNotFoundException, EntityNotFoundException {

        requireNonNull(incoming, "inbound is required");
        requireNonNull(label, "label is required");
        requireNonNull(outbound, "outbound is required");

        ArtemisVertex inboundVertex = getVertex().toVertex(incoming);
        ArtemisVertex outboundVertex = getVertex().toVertex(outbound);

        Object outboundId = outboundVertex.getId()
                .map(Value::get)
                .orElseThrow(() -> new NullPointerException("outbound Id field is required"));
        Object inboundId = inboundVertex.getId()
                .map(Value::get)
                .orElseThrow(() -> new NullPointerException("inbound Id field is required"));


        final Predicate<Traverser<Edge>> predicate = t -> {
            Edge e = t.get();
            return e.inVertex().id().equals(inboundId)
                    && e.outVertex().id().equals(outboundId);
        };

        Optional<Edge> edge = getGraph()
                .traversal().V(outboundId)
                .out(label).has(id, inboundId).inE(label).filter(predicate).tryNext();

        if (edge.isPresent()) {
            return new DefaultEdgeEntity<>(edge.get(), incoming, outbound);
        } else {

            Vertex inVertex = getGraph()
                    .traversal()
                    .V(inboundId)
                    .tryNext()
                    .orElseThrow(() -> new EntityNotFoundException("inbound entity not found"));

            Vertex outVertex = getGraph()
                    .traversal()
                    .V(outboundId)
                    .tryNext()
                    .orElseThrow(() -> new EntityNotFoundException("outbound entity not found"));

            return new DefaultEdgeEntity<>(outVertex.addEdge(label, inVertex), incoming, outbound);
        }


    }

    @Override
    public <OUT, IN, E> Optional<EdgeEntity<OUT, IN>> edge(E edgeId) throws NullPointerException {
        requireNonNull(edgeId, "edgeId is required");

        Optional<Edge> edgeOptional = getGraph().traversal().E(edgeId).tryNext();

        if (edgeOptional.isPresent()) {
            Edge edge = edgeOptional.get();
            return Optional.of(toEdgeEntity(edge, getVertex()));
        }

        return Optional.empty();
    }

    @Override
    public VertexTraversal getTraversalVertex(Object... vertexIds) throws NullPointerException {
        if (Stream.of(vertexIds).anyMatch(Objects::isNull)) {
            throw new NullPointerException("No one vertexId element cannot be null");
        }
        return new DefaultVertexTraversal(() -> getGraph().traversal().V(vertexIds), INITIAL_VERTEX, getVertex());
    }

    @Override
    public EdgeTraversal getTraversalEdge(Object... edgeIds) throws NullPointerException {
        if (Stream.of(edgeIds).anyMatch(Objects::isNull)) {
            throw new NullPointerException("No one edgeId element cannot be null");
        }
        return new DefaultEdgeTraversal(() -> getGraph().traversal().E(edgeIds), INITIAL_EDGE, getVertex());
    }


    private <T> void checkId(T entity) {
        ClassRepresentation classRepresentation = getClassRepresentations().get(entity.getClass());
        classRepresentation.getId().orElseThrow(() -> IdNotFoundException.newInstance(entity.getClass()));
    }
}
