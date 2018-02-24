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
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A structure to represents {@link org.apache.tinkerpop.gremlin.structure.Vertex} in memory
 */
public interface ArtemisVertex {

    /**
     * Returns the property from the key
     *
     * @param key the key to find the property
     * @return the property to the respective key otherwise {@link Optional#empty()}
     * @throws NullPointerException when key is null
     */
    Optional<Value> get(String key);

    /**
     * Add a new element in the Vertex
     *
     * @param key   the key
     * @param value the information
     * @throws NullPointerException when either key or value are null
     */
    void add(String key, Object value);

    /**
     * Add a new element in the Vertex
     *
     * @param key   the key
     * @param value the information
     * @throws NullPointerException when either key or value are null
     */
    void add(String key, Value value);

    /**
     * Add a new element in the Vertex
     *
     * @param property the element
     * @throws NullPointerException when either key or value are null
     */
    void add(Property property);

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
    Collection<Value> getValues();

    /**
     * Returns the label of the vertex
     *
     * @return the label
     */
    String getLabel();

    /**
     * Returns the id
     *
     * @return the id otherwise {@link Optional#empty()}
     */
    Optional<Value> getId();

    /**
     * Returns the properties of this vertex
     *
     * @return the properties
     */
    List<Property> getProperties();

    /**
     * creates a new Vertex
     *
     * @param label the label to the vertex
     * @return a new Vertex instance
     * @throws NullPointerException when label is null
     */
    static ArtemisVertex of(String label) {
        return new DefaultArtemisVertex(label);
    }

    /**
     * creates a new Vertex
     *
     * @param label the label to the vertex
     * @param id    the id of the vertex
     * @return a new Vertex instance
     * @throws NullPointerException when either label or id are null
     */
    static ArtemisVertex of(String label, Object id) {
        return new DefaultArtemisVertex(label, id);
    }

    /**
     * Returns the element size
     *
     * @return the size
     */
    int size();


    /**
     * Returns the value to which the specified key is mapped, or {link {@link Optional#empty()}} if this map contains no mapping for the key.
     *
     * @param key the key
     * @return the value
     * @throws NullPointerException when the key is null
     */
    Optional<Property> find(String key);

    /**
     * Removes the mapping for a key from this map if it is present (optional operation).
     *
     * @param key the key
     * @throws NullPointerException when key is null
     */
    void remove(String key);
}
