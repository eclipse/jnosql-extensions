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

import org.jnosql.artemis.EntityNotFoundException;
import org.jnosql.artemis.IdNotFoundException;

import java.util.Optional;

/**
 * This interface that represents the common operation between an entity
 * and {@link org.apache.tinkerpop.gremlin.structure.Vertex}
 */
public interface GraphTemplate {

    /**
     * Inserts entity
     *
     * @param entity entity to be saved
     * @param <T>    the instance type
     * @return the entity saved
     * @throws NullPointerException when document is null
     * @throws IdNotFoundException  when entity has not {@link org.jnosql.artemis.Id}
     */
    <T> T insert(T entity) throws NullPointerException, IdNotFoundException;

    /**
     * Updates entity
     *
     * @param entity entity to be updated
     * @param <T>    the instance type
     * @return the entity saved
     * @throws NullPointerException when document is null
     * @throws IdNotFoundException  when an entity is null
     */
    <T> T update(T entity) throws NullPointerException, IdNotFoundException;


    /**
     * Deletes a either {@link org.apache.tinkerpop.gremlin.structure.Vertex}
     *
     * @param id  the id to be used in the query {@link org.apache.tinkerpop.gremlin.structure.T#id}
     * @param <T> the id type
     * @throws NullPointerException when either label and id are null
     */
    <T> void delete(T id) throws NullPointerException;


    /**
     * Find an entity given {@link org.apache.tinkerpop.gremlin.structure.T#label} and
     * {@link org.apache.tinkerpop.gremlin.structure.T#id}
     *
     * @param id   the id to be used in the query {@link org.apache.tinkerpop.gremlin.structure.T#id}
     * @param <T>  the entity type
     * @param <ID> the id type
     * @return the entity found otherwise {@link Optional#empty()}
     * @throws NullPointerException when either label and id are null
     */
    <T, ID> Optional<T> find(ID id) throws NullPointerException;

    /**
     * Either find or create an Edge between this two entities.
     * {@link org.apache.tinkerpop.gremlin.structure.Edge}
     * <pre>entityOUT ---label---> entityIN.</pre>
     *
     * @param inbound  the inbound entity
     * @param label    the Edge label
     * @param outbound the outbound entity
     * @param <IN>     the ingoing type
     * @param <OUT>    the outgoing type
     * @return the {@link EdgeEntity} of these two entities
     * @throws NullPointerException    Either when any elements are null or the entity is null
     * @throws IdNotFoundException     when {@link org.jnosql.artemis.Id} annotation is missing in the entities
     * @throws EntityNotFoundException when neither outbound or inbound is found
     */
    <IN, OUT> EdgeEntity<IN, OUT> edge(OUT outbound, String label, IN inbound) throws NullPointerException,
            IdNotFoundException, EntityNotFoundException;


    /**
     * Gets a {@link VertexTraversal} to run a query in the graph
     *
     * @param vertexIds get ids
     * @return a {@link VertexTraversal} instance
     * @throws NullPointerException if any id element is null
     */
    VertexTraversal getTraversal(Object... vertexIds) throws NullPointerException;


}
