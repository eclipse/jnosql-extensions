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

import java.util.Objects;

import static java.util.Objects.requireNonNull;

class DefaultArtemisProperty implements ArtemisProperty {

    private final String key;

    private final Object value;

    DefaultArtemisProperty(String key, Object value) {
        this.key = requireNonNull(key, "key is required");
        this.value = requireNonNull(value, "value is required");
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Value getValue() {
        return Value.of(value);
    }

    @Override
    public <T> T get(TypeSupplier<T> typeSupplier) throws NullPointerException, UnsupportedOperationException {
        requireNonNull(typeSupplier, "typeSupplier is required");
        return Value.of(value).get(typeSupplier);
    }

    @Override
    public Object get() {
        return Value.of(value).get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultArtemisProperty)) {
            return false;
        }
        DefaultArtemisProperty that = (DefaultArtemisProperty) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultArtemisProperty{");
        sb.append("key='").append(key).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
