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

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * The default implementation of {@link GraphWorkflow}
 */
class DefaultGraphWorkflow implements GraphWorkflow {

    private GraphEventPersistManager graphEventPersistManager;

    private VertexConverter converter;


    DefaultGraphWorkflow() {
    }

    @Inject
    DefaultGraphWorkflow(GraphEventPersistManager graphEventPersistManager, VertexConverter converter) {
        this.graphEventPersistManager = graphEventPersistManager;
        this.converter = converter;
    }

    @Override
    public <T> T flow(T entity, UnaryOperator<ArtemisVertex> action) throws NullPointerException {
        Function<T, T> flow = getFlow(entity, action);
        return flow.apply(entity);
    }

    private <T> Function<T, T> getFlow(T entity, UnaryOperator<ArtemisVertex> action) {
        UnaryOperator<T> validation = t -> Objects.requireNonNull(t, "entity is required");

        UnaryOperator<T> firePreEntity = t -> {
            graphEventPersistManager.firePreEntity(t);
            return t;
        };

        UnaryOperator<T> firePreColumnEntity = t -> {
            graphEventPersistManager.firePreGraphEntity(t);
            return t;
        };

        Function<T, ArtemisVertex> converterColumn = t -> converter.toVertex(t);

        UnaryOperator<ArtemisVertex> firePreDocument = t -> {
            graphEventPersistManager.firePreGraph(t);
            return t;
        };

        UnaryOperator<ArtemisVertex> firePostDocument = t -> {
            graphEventPersistManager.firePostGraph(t);
            return t;
        };

        Function<ArtemisVertex, T> converterEntity = t -> converter.toEntity((Class<T>) entity.getClass(), t);

        UnaryOperator<T> firePostEntity = t -> {
            graphEventPersistManager.firePostEntity(t);
            return t;
        };

        UnaryOperator<T> firePostColumnEntity = t -> {
            graphEventPersistManager.firePostGraphEntity(t);
            return t;
        };

        return validation
                .andThen(firePreEntity)
                .andThen(firePreColumnEntity)
                .andThen(converterColumn)
                .andThen(firePreDocument)
                .andThen(action)
                .andThen(firePostDocument)
                .andThen(converterEntity)
                .andThen(firePostEntity)
                .andThen(firePostColumnEntity);
    }
}
