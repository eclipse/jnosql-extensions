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

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

/**
 * The default {@link GraphTemplate}
 */
class DefaultGraphTemplate extends AbstractGraphTemplate {


    @Inject
    private Instance<GraphTraversalSource> graphTraversal;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private GraphConverter converter;

    @Inject
    private GraphWorkflow workflow;

    @Inject
    private Reflections reflections;


    @Override
    public final GraphTraversalSource getTraversalSource() {
        return graphTraversal.get();
    }

    @Override
    protected ClassRepresentations getClassRepresentations() {
        return classRepresentations;
    }

    @Override
    protected GraphConverter getConverter() {
        return converter;
    }

    @Override
    protected GraphWorkflow getFlow() {
        return workflow;
    }

    @Override
    protected Reflections getReflections() {
        return reflections;
    }
}
