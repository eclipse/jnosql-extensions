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

import org.jnosql.diana.api.Value;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A structure to represents {@link org.apache.tinkerpop.gremlin.structure.Vertex} in memory
 */
interface ArtemisVertex {

    /**
     * Returns the property from the key
     *
     * @param key the key to find the property
     * @return the property to the respective key otherwise {@link Optional#empty()}
     * @throws NullPointerException when key is null
     */
    Optional<Value> get(String key) throws NullPointerException;

    /**
     * Add a new element in the Vertex
     *
     * @param key   the key
     * @param value the information
     * @throws NullPointerException when either key or value are null
     */
    void add(String key, Object value) throws NullPointerException;

    /**
     * Add a new element in the Vertex
     *
     * @param key   the key
     * @param value the information
     * @throws NullPointerException when either key or value are null
     */
    void add(String key, Value value) throws NullPointerException;

    /**
     * Returns a Set view of the keys contained in this instance
     *
     * @return the keys
     */
    Set<String> getKeys();

    /**
     * Returns a Collection view of the values in this instance
     *
     * @return the values
     */
    Collection<Value> getValue();

    /**
     * Returns a Set view of the keys contained in this instance
     *
     * @return the keys
     */
    String getLabel();

}
