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

import org.jnosql.diana.api.TypeSupplier;
import org.jnosql.diana.api.Value;

import static java.util.Objects.requireNonNull;

/**
 * The tuple that represent a property in the Graph Mapping layer.
 */
public interface Property {

    /**
     * Gets the key
     *
     * @return the key
     */
    String getKey();

    /**
     * Gets the value
     *
     * @return the value
     */
    Value getValue();


    /**
     * Creates a Property from key and value
     * @param key the key
     * @param value the value
     * @return the Property instance
     * @throws NullPointerException when either key or value are null
     */
    static Property of(String key, Object value) {
        requireNonNull(key, "key is required");
        requireNonNull(value, "value is required");
        if (value instanceof Value) {
            return new DefaultProperty(key, Value.class.cast(value).get());
        }
        return new DefaultProperty(key, value);
    }
    /**
     * Alias to {@link org.jnosql.diana.api.Value#get(TypeSupplier)}
     *
     * @param typeSupplier {@link org.jnosql.diana.api.Value#get(TypeSupplier)}
     * @param <T>          {@link org.jnosql.diana.api.Value#get(TypeSupplier)}
     * @return {@link org.jnosql.diana.api.Value#get(TypeSupplier)}
     * @throws NullPointerException          when type is null
     * @throws UnsupportedOperationException when {@link org.jnosql.diana.api.Value#get(TypeSupplier)} has not support
     */
    <T> T get(TypeSupplier<T> typeSupplier);


    /**
     * Alias to {@link org.jnosql.diana.api.Value#get()}
     *
     * @return {@link org.jnosql.diana.api.Value#get()}
     */
    Object get();
}
