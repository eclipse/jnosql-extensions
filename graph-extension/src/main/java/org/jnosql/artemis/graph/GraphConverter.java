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

public interface GraphConverter {

    /**
     * Converts entity object to  TinkerPop Vertex
     *
     * @param entity the entity
     * @param <T>    the entity type
     * @return the ThinkerPop Vertex with the entity values
     * @throws NullPointerException when entity is null
     */
    <T> Vertex toVertex(T entity);

    /**
     * Converts vertex to an entity
     *
     * @param vertex the vertex
     * @param <T>    the entity type
     * @return a entity instance
     * @throws NullPointerException when vertex is null
     */
    <T> T toEntity(Vertex vertex);


    /**
     *
     * @param edge
     * @return
     * @throws NullPointerException
     */
    EdgeEntity toEdgeEntity(Edge edge);

    /**
     *
     * @param edge
     * @return
     * @throws NullPointerException
     */
    Edge toEdge(EdgeEntity edge);


}
