package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.function.Supplier;

public interface GraphTraversalSourceSupplier extends Supplier<GraphTraversalSource> {

}
