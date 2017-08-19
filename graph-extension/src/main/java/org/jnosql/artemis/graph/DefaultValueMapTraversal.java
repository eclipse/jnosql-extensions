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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;

/**
 * The default implementation of {@link ValueMapTraversal}
 */
class DefaultValueMapTraversal implements ValueMapTraversal{

    private final Supplier<GraphTraversal<?, ?>> supplier;
    private final Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Map<String, Object>>> flow;

    DefaultValueMapTraversal(Supplier<GraphTraversal<?, ?>> supplier, Function<GraphTraversal<?, ?>,
            GraphTraversal<Vertex, Map<String, Object>>> flow) {
        this.supplier = supplier;
        this.flow = flow;
    }


    @Override
    public Stream<Map<String, Object>> stream() {
        return flow.apply(supplier.get()).toList().stream();
    }

    @Override
    public Stream<Map<String, Object>> stream(int limit) {
        return flow.apply(supplier.get()).next(limit).stream();
    }

    @Override
    public Map<String, Object> next() {
        return flow.apply(supplier.get()).tryNext().orElse(emptyMap());
    }

    @Override
    public long count() {
        return flow.apply(supplier.get()).count().tryNext().orElse(0L);
    }
}
