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

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
class DefaultGraphConverter implements GraphConverter {

    @Override
    public <T> Vertex toVertex(T entity) {
        return null;
    }

    @Override
    public <T> T toEntity(Vertex vertex) {
        return null;
    }

    @Override
    public EdgeEntity toEdgeEntity(Edge edge) {
        return null;
    }

    @Override
    public Edge toEdge(EdgeEntity edge) {
        return null;
    }
}
