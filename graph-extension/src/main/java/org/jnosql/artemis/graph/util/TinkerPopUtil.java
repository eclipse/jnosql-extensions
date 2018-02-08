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
package org.jnosql.artemis.graph.util;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.graph.ArtemisVertex;
import org.jnosql.artemis.graph.EdgeEntity;
import org.jnosql.artemis.graph.VertexConverter;
import org.jnosql.diana.api.Value;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.apache.tinkerpop.gremlin.structure.T.id;
import static org.apache.tinkerpop.gremlin.structure.T.label;


/**
 * Utils class to integrate Artemis with TinkerPop
 */
public final class TinkerPopUtil {

    private TinkerPopUtil() {
    }

    /**
     * Converts Vertex to ArtemisVertex
     *
     * @param vertex the TinkerPop Vertex
     * @return the Artemis Vertex
     * @throws NullPointerException when vertex is null
     */
    public static ArtemisVertex toArtemisVertex(Vertex vertex) {
        requireNonNull(vertex, "vertex is required");


        ArtemisVertex artemisVertex = ofNullable(vertex.id())
                .map(id -> ArtemisVertex.of(vertex.label(), id))
                .orElse(ArtemisVertex.of(vertex.label()));

        vertex.keys().forEach(k -> artemisVertex.add(k, Value.of(vertex.value(k))));
        return artemisVertex;
    }

    /**
     * Converts {@link ArtemisVertex} to {@link Vertex}
     *
     * @param artemisVertex the vertex
     * @param graph         the graph the graph
     * @return the {@link Vertex} with {@link ArtemisVertex} information
     * @throws NullPointerException when either vertex or graph are null
     */
    public static Vertex toVertex(ArtemisVertex artemisVertex, Graph graph) {

        requireNonNull(artemisVertex, "artemisVertex is required");
        requireNonNull(graph, "graph is required");
        Vertex vertex = artemisVertex.getId().map(v -> graph.addVertex(label, artemisVertex.getLabel(), id, v.get()))
                .orElse(graph.addVertex(artemisVertex.getLabel()));

        artemisVertex.getProperties()
                .forEach(p -> vertex.property(p.getKey(), p.get()));

        return vertex;
    }

    /**
     * Converts Vertex to an entity
     *
     * @param vertex    the TinkerPop entity
     * @param converter the converter
     * @param <T>       the entity type
     * @return the Entity
     * @throws NullPointerException when either vertex and converter are null
     */
    public static <T> T toEntity(Vertex vertex, VertexConverter converter) {
        requireNonNull(vertex, "vertex is required");
        requireNonNull(converter, "converter is required");
        return converter.toEntity(toArtemisVertex(vertex));
    }

    /**
     * Converts Edge to EdgeEntity
     *
     * @param edge      the edge TinkerPop structure
     * @param converter the converts
     * @return the {@link EdgeEntity} instance
     */
    public static EdgeEntity toEdgeEntity(Edge edge, VertexConverter converter) {

        requireNonNull(edge, "edge is required");
        requireNonNull(converter, "converter is required");

        ArtemisVertex inVertex = toArtemisVertex(edge.inVertex());
        ArtemisVertex outVertex = toArtemisVertex(edge.outVertex());
        return EdgeEntity.of(converter.toEntity(outVertex), edge, converter.toEntity(inVertex));

    }
}
