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
 * This interface represents the converter between an entity and the {@link ArtemisGraph}
 */
public interface GraphConverter {

    /**
     * Converts the instance entity to {@link ArtemisGraph}
     *
     * @param entityInstance the instnace
     * @return a {@link ArtemisGraph} instance
     * @throws NullPointerException when entityInstance is null
     */
    ArtemisGraph toGraph(Object entityInstance) throws NullPointerException;

    /**
     * Converts a {@link ArtemisGraph} to entity
     *
     * @param entityClass the entity class
     * @param entity      the {@link ArtemisGraph} to be converted
     * @param <T>         the entity type
     * @return the instance from {@link ArtemisGraph}
     * @throws NullPointerException when either entityClass or entity are null
     */
    <T> T toEntity(Class<T> entityClass, ArtemisGraph entity) throws NullPointerException;

    /**
     * Similar to {@link GraphConverter#toEntity(Class, ArtemisGraph)}, but
     * search the instance type from {@link ArtemisGraph#getLabel()}
     *
     * @param entity the {@link ArtemisGraph} to be converted
     * @param <T>    the entity type
     * @return the instance from {@link ArtemisGraph}
     * @throws NullPointerException when entity is null
     */
    <T> T toEntity(ArtemisGraph entity) throws NullPointerException;
}
