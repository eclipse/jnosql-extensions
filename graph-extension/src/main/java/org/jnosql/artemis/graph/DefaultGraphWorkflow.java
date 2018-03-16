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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * The default implementation of {@link GraphWorkflow}
 */
class DefaultGraphWorkflow implements GraphWorkflow {

    private GraphEventPersistManager graphEventPersistManager;

    private GraphConverter converter;


    DefaultGraphWorkflow() {
    }

    @Inject
    DefaultGraphWorkflow(GraphEventPersistManager graphEventPersistManager, GraphConverter converter) {
        this.graphEventPersistManager = graphEventPersistManager;
        this.converter = converter;
    }

    @Override
    public <T> T flow(T entity, Optional<Vertex> vertex, UnaryOperator<Vertex> action) {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(vertex, "vartex is required");
        Objects.requireNonNull(action, "action is required");
        
        Function<T, T> flow = getFlow(entity, vertex, action );
        return flow.apply(entity);
    }

    private <T> Function<T, T> getFlow(T entity, Optional<Vertex> vertex, UnaryOperator<Vertex> action) {

        UnaryOperator<T> firePreEntity = t -> {
            graphEventPersistManager.firePreEntity(t);
            return t;
        };

        UnaryOperator<T> firePreGraphEntity = t -> {
            graphEventPersistManager.firePreGraphEntity(t);
            return t;
        };

        UnaryOperator<Vertex> firePreGraph = t -> {
            if( t != null ) graphEventPersistManager.firePreGraph(t);
            return t;
        };

        UnaryOperator<Vertex> firePostGraph = t -> {
            graphEventPersistManager.firePostGraph(t);
            return t;
        };

        UnaryOperator<T> firePostEntity = t -> {
            graphEventPersistManager.firePostEntity(t);
            return t;
        };

        UnaryOperator<T> firePostGraphEntity = t -> {
            graphEventPersistManager.firePostGraphEntity(t);
            return t;
        };

        return  firePreEntity
                .andThen(firePreGraphEntity)
                .andThen(v -> vertex.orElseGet( () -> null ) )
                .andThen(firePreGraph)
                .andThen(action)
                .andThen(firePostGraph)
                .andThen(v -> entity)
                .andThen(firePostEntity)
                .andThen(firePostGraphEntity);
    }
}
