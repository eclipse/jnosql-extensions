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



/**
 * This interface represents the converter between an entity and the {@link ArtemisVertex}
 */
public interface VertexConverter {

    /**
     * Converts the instance entity to {@link ArtemisVertex}
     *
     * @param entityInstance the instnace
     * @return a {@link ArtemisVertex} instance
     * @throws NullPointerException when entityInstance is null
     */
    ArtemisVertex toVertex(Object entityInstance) throws NullPointerException;

    /**
     * Converts a {@link ArtemisVertex} to entity
     *
     * @param entityClass the entity class
     * @param entity      the {@link ArtemisVertex} to be converted
     * @param <T>         the entity type
     * @return the instance from {@link ArtemisVertex}
     * @throws NullPointerException when either entityClass or entity are null
     */
    <T> T toEntity(Class<T> entityClass, ArtemisVertex entity) throws NullPointerException;

    /**
     * Similar to {@link VertexConverter#toEntity(Class, ArtemisVertex)}, but
     * search the instance type from {@link ArtemisVertex#getLabel()}
     *
     * @param entity the {@link ArtemisVertex} to be converted
     * @param <T>    the entity type
     * @return the instance from {@link ArtemisVertex}
     * @throws NullPointerException when entity is null
     */
    <T> T toEntity(ArtemisVertex entity) throws NullPointerException;
}
