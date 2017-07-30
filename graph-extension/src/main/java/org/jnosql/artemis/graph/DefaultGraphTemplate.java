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

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.EntityNotFoundException;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Value;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.tinkerpop.gremlin.structure.T.id;
import static org.apache.tinkerpop.gremlin.structure.T.label;
import static org.jnosql.artemis.graph.TinkerPopUtil.toArtemisVertex;

/**
 * The default {@link GraphTemplate}
 */
class DefaultGraphTemplate implements GraphTemplate {

    @Inject
    private Instance<Graph> graph;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private VertexConverter converter;

    @Override
    public <T> T insert(T entity) throws NullPointerException, IdNotFoundException {
        requireNonNull(entity, "entity is required");
        checkId(entity);

        ArtemisVertex artemisVertex = converter.toVertex(entity);

        Vertex vertex = artemisVertex.getId().map(v -> graph.get().addVertex(label, artemisVertex.getLabel(), id, v.get()))
                .orElse(graph.get().addVertex(artemisVertex.getLabel()));

        artemisVertex.getProperties().stream().forEach(e -> vertex.property(e.getKey(), e.get()));


        ArtemisVertex vertexUpdated = toArtemisVertex(vertex);

        return converter.toEntity(vertexUpdated);
    }

    @Override
    public <T> T update(T entity) throws NullPointerException, IdNotFoundException {
        requireNonNull(entity, "entity is required");
        checkId(entity);

        ArtemisVertex artemisVertex = converter.toVertex(entity);
        Object idValue = artemisVertex.getId()
                .map(Value::get)
                .orElseThrow(() -> new NullPointerException("Id field is required"));

        Vertex vertex = graph.get()
                .traversal()
                .V(idValue)
                .tryNext()
                .orElseThrow(() -> new EntityNotFoundException(format("The entity %s with id %s is not found to update",
                        entity.getClass().getName(), idValue.toString())));

        artemisVertex.getProperties().stream().forEach(e -> vertex.property(e.getKey(), e.get()));

        return entity;
    }

    @Override
    public <T> void delete(T idValue) throws NullPointerException {
        requireNonNull(label, "label is required");
        requireNonNull(idValue, "id is required");
        List<Vertex> vertices = graph.get().traversal().V(idValue).toList();
        vertices.stream().forEach(Vertex::remove);

    }

    @Override
    public <T, ID> Optional<T> find(ID idValue) throws NullPointerException {
        requireNonNull(label, "label is required");
        requireNonNull(idValue, "id is required");
        Optional<Vertex> vertex = graph.get().traversal().V(idValue).tryNext();
        if (vertex.isPresent()) {
            return Optional.of(converter.toEntity(toArtemisVertex(vertex.get())));
        }
        return Optional.empty();
    }

    @Override
    public <OUT, IN> EdgeEntity<OUT, IN> edge(OUT outbound, String label, IN inbound) throws NullPointerException,
            IdNotFoundException, EntityNotFoundException {

        requireNonNull(inbound, "inbound is required");
        requireNonNull(label, "label is required");
        requireNonNull(outbound, "outbound is required");

        ArtemisVertex inboundVertex = converter.toVertex(inbound);
        ArtemisVertex outboundVertex = converter.toVertex(outbound);

        Object outboundId = outboundVertex.getId()
                .map(Value::get)
                .orElseThrow(() -> new NullPointerException("outbound Id field is required"));
        Object inboundId = inboundVertex.getId()
                .map(Value::get)
                .orElseThrow(() -> new NullPointerException("inbound Id field is required"));


        Optional<Edge> edge = graph.get()
                .traversal().V()
                .has(id, outboundId).out(label)
                .has(id, inboundId).inE(label).tryNext();

        if (edge.isPresent()) {
            return new DefaultEdgeEntity<>(edge.get(), inbound, outbound);
        } else {

            Vertex inVertex = graph.get()
                    .traversal()
                    .V(inboundId)
                    .tryNext()
                    .orElseThrow(() -> new EntityNotFoundException("inbound entity not found"));

            Vertex outVertex = graph.get()
                    .traversal()
                    .V(outboundId)
                    .tryNext()
                    .orElseThrow(() -> new EntityNotFoundException("outbound entity not found"));

            return new DefaultEdgeEntity<>(outVertex.addEdge(label, inVertex), inbound, outbound);
        }


    }

    @Override
    public VertexTraversal getTraversal(Object... vertexIds) throws NullPointerException {
        if (Stream.of(vertexIds).anyMatch(Objects::isNull)) {
            throw new NullPointerException("No one vertexId element cannot be null");
        }
        return new DefaultVertexTraversal(() -> graph.get().traversal().V(vertexIds), Function.identity(), converter);
    }


    private <T> void checkId(T entity) {
        ClassRepresentation classRepresentation = classRepresentations.get(entity.getClass());
        classRepresentation.getId().orElseThrow(() -> IdNotFoundException.newInstance(entity.getClass()));
    }


}
