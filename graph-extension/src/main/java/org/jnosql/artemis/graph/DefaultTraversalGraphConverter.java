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
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * A default implementation using DefaultTraversalGraphConverter
 */
@GraphTraversalOperation
class DefaultTraversalGraphConverter extends AbstractGraphConverter implements GraphConverter {


    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Inject
    private Converters converters;

    @Inject
    private Instance<Graph> graph;


    @Override
    protected ClassRepresentations getClassRepresentations() {
        return classRepresentations;
    }

    @Override
    protected Reflections getReflections() {
        return reflections;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @Override
    protected Graph getGraph() {
        return graph.get();
    }

    @Override
    public <T> Vertex toVertex(T entity) {
        requireNonNull(entity, "entity is required");

        ClassRepresentation representation = getClassRepresentations().get(entity.getClass());
        List<FieldGraph> fields = representation.getFields().stream()
                .map(f -> to(f, entity))
                .filter(FieldGraph::isNotEmpty).collect(toList());

        final Property id = fields.stream()
                .filter(FieldGraph::isId)
                .findFirst()
                .map(i -> i.toElement(getConverters()))
                .orElseThrow(() -> new IllegalArgumentException("Entity has not a valid Id"));
        return graph.get().traversal().V(id.value()).next();

    }


}
