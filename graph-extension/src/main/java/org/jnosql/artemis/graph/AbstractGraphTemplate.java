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

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.apache.tinkerpop.gremlin.structure.T.id;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.EntityNotFoundException;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;

public abstract class AbstractGraphTemplate implements GraphTemplate {

    @SuppressWarnings("unchecked")
    private static final Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Vertex>> INITIAL_VERTEX =
            g -> (GraphTraversal<Vertex, Vertex>) g;

    @SuppressWarnings("unchecked")
    private static final Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Edge>> INITIAL_EDGE =
            g -> (GraphTraversal<Vertex, Edge>) g;


    protected abstract ClassRepresentations getClassRepresentations();

    protected abstract GraphConverter getConverter();

    protected abstract GraphWorkflow getFlow();

    protected abstract Reflections getReflections();
    
    /**
     * 
     */
    @Override
    public <T> T toEntity(Vertex vertex) {
        
        requireNonNull(vertex, "vertex is required");
    
        return getConverter().toEntity(vertex);
    }

    @Override
    public <T> T insert(T entity) {
        requireNonNull(entity, "entity is required");
        checkId(entity);
        
        final UnaryOperator<Vertex> insert = v -> getConverter().addVertex(entity);

        return getFlow().flow(entity, Optional.empty(), insert);
    }

    @Override
    public <T> T update(T entity) {
        requireNonNull(entity, "entity is required");
        checkId(entity);
        if (isIdNull(entity)) {
            throw new NullPointerException("to update a graph id cannot be null");
        }
        final Vertex vertex = getVertex(entity).orElseThrow(() -> new EntityNotFoundException("Entity does not find in the update"));

        final UnaryOperator<Vertex> update =  v -> getConverter().updateVertex(entity, v);
            
        return getFlow().flow(entity, Optional.of(vertex), update );
    }

    @Override
    public <T> void delete(T idValue) {
        requireNonNull(idValue, "id is required");
        List<Vertex> vertices = getTraversalSource().V(idValue).toList();
        vertices.forEach(Vertex::remove);

    }

    @Override
    public <T> void deleteEdge(T idEdge) {
        requireNonNull(idEdge, "idEdge is required");
        getTraversalSource().E(idEdge).drop().iterate();
    }

    @Override
    public <T, ID> Optional<T> find(ID idValue) {
        requireNonNull(idValue, "id is required");
        Optional<Vertex> vertex = getTraversalSource().V(idValue).tryNext();
        return vertex.map(getConverter()::toEntity);
    }

    @Override
    public <OUT, IN> EdgeEntity edge(OUT outgoing, String label, IN incoming) {

        requireNonNull(incoming, "incoming is required");
        requireNonNull(label, "label is required");
        requireNonNull(outgoing, "outgoing is required");

        checkId(outgoing);
        checkId(incoming);

        if (isIdNull(outgoing)) {
            throw new NullPointerException("outgoing Id field is required");
        }

        if (isIdNull(incoming)) {
            throw new NullPointerException("incoming Id field is required");
        }


        Vertex outVertex = getVertex(outgoing).orElseThrow(() -> new EntityNotFoundException("Outgoing entity does not found"));
        Vertex inVertex = getVertex(incoming).orElseThrow(() -> new EntityNotFoundException("Incoming entity does not found"));

        final Predicate<Traverser<Edge>> predicate = t -> {
            Edge e = t.get();
            return e.inVertex().id().equals(inVertex.id())
                    && e.outVertex().id().equals(outVertex.id());
        };

        Optional<Edge> edge = getTraversalSource()
                .V(outVertex.id())
                .out(label).has(id, inVertex.id()).inE(label).filter(predicate).tryNext();

        return edge.<EdgeEntity>map(edge1 -> new DefaultEdgeEntity<>(edge1, incoming, outgoing))
                .orElseGet(() -> 
                    new DefaultEdgeEntity<>( getTraversalSource().V(outVertex.id()).as("out").V(inVertex.id()).addE(label).from("out").next(), incoming, outgoing));


    }

    @Override
    public <E> Optional<EdgeEntity> edge(E edgeId) {
        requireNonNull(edgeId, "edgeId is required");

        Optional<Edge> edgeOptional = getTraversalSource().E(edgeId).tryNext();

        if (edgeOptional.isPresent()) {
            Edge edge = edgeOptional.get();
            return Optional.of(getConverter().toEdgeEntity(edge));
        }

        return Optional.empty();
    }


    @Override
    public <T> Collection<EdgeEntity> getEdges(T entity, Direction direction) {
        return getEdgesImpl(entity, direction);
    }

    @Override
    public <T> Collection<EdgeEntity> getEdges(T entity, Direction direction, String... labels) {
        return getEdgesImpl(entity, direction, labels);
    }


    @SafeVarargs
    @Override
    public final <T> Collection<EdgeEntity> getEdges(T entity, Direction direction, Supplier<String>... labels) {
        checkLabelsSupplier(labels);
        return getEdgesImpl(entity, direction, Stream.of(labels).map(Supplier::get).toArray(String[]::new));
    }


    @Override
    public <ID> Collection<EdgeEntity> getEdgesById(ID id, Direction direction, String... labels) {
        return getEdgesByIdImpl(id, direction, labels);
    }

    @Override
    public <ID> Collection<EdgeEntity> getEdgesById(ID id, Direction direction) {
        return getEdgesByIdImpl(id, direction);
    }

    @SafeVarargs
    @Override
    public final <ID> Collection<EdgeEntity> getEdgesById(ID id, Direction direction, Supplier<String>... labels) {
        checkLabelsSupplier(labels);
        return getEdgesByIdImpl(id, direction, Stream.of(labels).map(Supplier::get).toArray(String[]::new));
    }


    @Override
    public VertexTraversal getTraversalVertex(Object... vertexIds) {
        if (Stream.of(vertexIds).anyMatch(Objects::isNull)) {
            throw new NullPointerException("No one vertexId element cannot be null");
        }
        return new DefaultVertexTraversal(() -> getTraversalSource().V(vertexIds), INITIAL_VERTEX, getConverter());
    }

    @Override
    public EdgeTraversal getTraversalEdge(Object... edgeIds) {
        if (Stream.of(edgeIds).anyMatch(Objects::isNull)) {
            throw new NullPointerException("No one edgeId element cannot be null");
        }
        return new DefaultEdgeTraversal(() -> getTraversalSource().E(edgeIds), INITIAL_EDGE, getConverter());
    }

    private <ID> Collection<EdgeEntity> getEdgesByIdImpl(ID id, Direction direction, String... labels) {

        requireNonNull(id, "id is required");
        requireNonNull(direction, "direction is required");

        GraphTraversal<Vertex,Edge> edges; 
                
        switch( direction ) {
        case IN:   
            edges = getTraversalSource().V(id).inE(labels);
            break;
        case OUT:
            edges = getTraversalSource().V(id).outE(labels);
            break;
        case BOTH:
        default:
            edges = getTraversalSource().V(id).bothE(labels);            
        }

        final Optional<List<Edge>> result = edges.fold().tryNext();
        
        if( result.isPresent() ) {
            return result.get().stream().map(getConverter()::toEdgeEntity).collect(Collectors.toList());
        }
        
        return Collections.emptyList();
    }

    private <T> Collection<EdgeEntity> getEdgesImpl(T entity, Direction direction, String... labels) {
        requireNonNull(entity, "entity is required");

        if (isIdNull(entity)) {
            throw new NullPointerException("Entity id is required");
        }

        if (!getVertex(entity).isPresent()) {
            return Collections.emptyList();
        }
        Object id = getConverter().toVertex(entity).id();
        return getEdgesByIdImpl(id, direction, labels);
    }

    private void checkLabelsSupplier(Supplier<String>[] labels) {
        if (Stream.of(labels).anyMatch(Objects::isNull)) {
            throw new NullPointerException("Item cannot be null");
        }
    }

    private <T> boolean isIdNull(T entity) {
        ClassRepresentation classRepresentation = getClassRepresentations().get(entity.getClass());
        FieldRepresentation field = classRepresentation.getId().get();
        return isNull(getReflections().getValue(entity, field.getNativeField()));

    }

    private <T> Optional<Vertex> getVertex(T entity) {
        ClassRepresentation classRepresentation = getClassRepresentations().get(entity.getClass());
        FieldRepresentation field = classRepresentation.getId().get();
        Object id = getReflections().getValue(entity, field.getNativeField());
        return getTraversalSource().V(id).tryNext();
    }

    private <T> void checkId(T entity) {
        ClassRepresentation classRepresentation = getClassRepresentations().get(entity.getClass());
        classRepresentation.getId().orElseThrow(() -> IdNotFoundException.newInstance(entity.getClass()));
    }
}
