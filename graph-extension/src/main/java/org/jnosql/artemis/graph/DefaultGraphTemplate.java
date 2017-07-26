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

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.Value;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static java.util.Objects.requireNonNull;
import static org.apache.tinkerpop.gremlin.structure.T.id;
import static org.apache.tinkerpop.gremlin.structure.T.label;

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


        ArtemisVertex vertexUpdated = ArtemisVertex.of(vertex.label(), vertex.id());

        for (String key : vertex.keys()) {
            Object value = vertex.value(key);
            ArtemisElement element = ArtemisElement.of(key, value);
            vertexUpdated.add(element);
        }
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

        Vertex vertex = graph.get().traversal().V().hasLabel(artemisVertex.getLabel()).has(id, idValue).next();
        artemisVertex.getProperties().stream().forEach(e -> vertex.property(e.getKey(), e.get()));

        return entity;
    }

    @Override
    public <T> void delete(String label, T idValue) throws NullPointerException {
        requireNonNull(label, "label is required");
        requireNonNull(idValue, "id is required");

        graph.get().traversal().V().hasLabel(label).has(id, idValue).remove();

    }

    private <T> void checkId(T entity) {
        ClassRepresentation classRepresentation = classRepresentations.get(entity.getClass());
        classRepresentation.getId().orElseThrow(() -> IdNotFoundException.newInstance(entity.getClass()));
    }

}
