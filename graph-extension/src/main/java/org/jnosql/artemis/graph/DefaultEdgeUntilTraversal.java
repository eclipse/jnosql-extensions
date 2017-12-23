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

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.function.Function;
import java.util.function.Supplier;

class DefaultEdgeUntilTraversal implements EdgeUntilTraversal {

    private final Supplier<GraphTraversal<?, ?>> supplier;
    private final Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Edge>> flow;
    private final VertexConverter converter;

    DefaultEdgeUntilTraversal(Supplier<GraphTraversal<?, ?>> supplier,
                                   Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Edge>> flow,
                                   VertexConverter converter) {
        this.supplier = supplier;
        this.flow = flow;
        this.converter = converter;
    }

    @Override
    public EdgeTraversal has(String propertyKey, Object value) throws NullPointerException {
        return null;
    }

    @Override
    public EdgeTraversal has(String propertyKey, P<?> predicate) throws NullPointerException {
        return null;
    }

    @Override
    public EdgeTraversal has(T accessor, Object value) throws NullPointerException {
        return null;
    }

    @Override
    public EdgeTraversal has(T accessor, P<?> predicate) throws NullPointerException {
        return null;
    }

    @Override
    public EdgeTraversal hasNot(String propertyKey) throws NullPointerException {
        return null;
    }
}
