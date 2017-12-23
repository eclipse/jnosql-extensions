package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The base class to {@link EdgeTraversal}
 */
abstract class AbstractEdgeTraversal {

    protected final Supplier<GraphTraversal<?, ?>> supplier;
    protected final Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Edge>> flow;
    protected final VertexConverter converter;

    AbstractEdgeTraversal(Supplier<GraphTraversal<?, ?>> supplier,
                                   Function<GraphTraversal<?, ?>, GraphTraversal<Vertex, Edge>> flow,
                                   VertexConverter converter) {
        this.supplier = supplier;
        this.flow = flow;
        this.converter = converter;
    }
}
