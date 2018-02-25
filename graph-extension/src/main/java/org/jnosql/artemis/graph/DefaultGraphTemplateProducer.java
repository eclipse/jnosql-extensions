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
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of {@link GraphTemplateProducer}
 */
class DefaultGraphTemplateProducer implements GraphTemplateProducer {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Inject
    private Converters converters;

    @Inject
    private GraphEventPersistManager persistManager;

    @Override
    public GraphTemplate get(Graph graph) {
        requireNonNull(graph, "graph is required");

        GraphConverter converter = new ProducerGraphConverter(classRepresentations, reflections, converters, graph);
        GraphWorkflow workflow = new DefaultGraphWorkflow(persistManager, converter);
        return new ProducerGraphTemplate(classRepresentations, converter, workflow, graph, reflections);
    }


    @Vetoed
    static class ProducerGraphTemplate extends AbstractGraphTemplate {

        private ClassRepresentations classRepresentations;

        private GraphConverter converter;

        private Graph graph;

        private GraphWorkflow workflow;

        private Reflections reflections;

        ProducerGraphTemplate(ClassRepresentations classRepresentations,
                              GraphConverter converter,
                              GraphWorkflow workflow,
                              Graph graph, Reflections reflections) {

            this.classRepresentations = classRepresentations;
            this.converter = converter;
            this.graph = graph;
            this.workflow = workflow;
            this.reflections = reflections;
        }

        ProducerGraphTemplate() {
        }

        @Override
        protected Graph getGraph() {
            return graph;
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

    @Vetoed
    static class ProducerGraphConverter extends AbstractGraphConverter implements GraphConverter {

        private ClassRepresentations classRepresentations;

        private Reflections reflections;

        private Converters converters;

        private Graph graph;

        public ProducerGraphConverter(ClassRepresentations classRepresentations,
                                      Reflections reflections, Converters converters,
                                      Graph graph) {
            this.classRepresentations = classRepresentations;
            this.reflections = reflections;
            this.converters = converters;
            this.graph = graph;
        }

        ProducerGraphConverter() {
        }

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
            return graph;
        }
    }
}
