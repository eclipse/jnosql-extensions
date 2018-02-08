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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * The default implementation of {@link ArtemisVertex}
 */
class DefaultArtemisVertex implements ArtemisVertex {


    private final Map<String, Object> properties = new HashMap<>();

    private final Object id;

    private final String label;

    DefaultArtemisVertex(String label) {
        requireNonNull(label, "label is required");
        this.label = label;
        this.id = null;
    }

    DefaultArtemisVertex(String label, Object id) {
        this.label = requireNonNull(label, "label is required");
        this.id = requireNonNull(id, "id is required");
    }

    @Override
    public Optional<Value> get(String key) {
        requireNonNull(key, "key is required");
        Object value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Value.of(value));
    }

    @Override
    public void add(String key, Object value) {
        requireNonNull(key, "key is required");
        requireNonNull(value, "value is required");
        properties.put(key, value);
    }

    @Override
    public void add(String key, Value value) {
        requireNonNull(key, "key is required");
        requireNonNull(value, "value is required");
        properties.put(key, value.get());
    }

    @Override
    public void add(ArtemisProperty element) {
        requireNonNull(element, "element is required");
        properties.put(element.getKey(), element.get());
    }

    @Override
    public Set<String> getKeys() {
        return unmodifiableSet(properties.keySet());
    }

    @Override
    public Collection<Value> getValues() {
        return properties
                .values()
                .stream()
                .map(Value::of)
                .collect(collectingAndThen(toList(),
                        Collections::unmodifiableList));

    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Optional<Value> getId() {

        if (id == null) {
            return Optional.empty();
        }
        return Optional.of(Value.of(id));
    }

    @Override
    public List<ArtemisProperty> getProperties() {
        return properties.entrySet().stream()
                .map(e -> ArtemisProperty.of(e.getKey(), e.getValue()))
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public Optional<ArtemisProperty> find(String key) {
        requireNonNull(key, "key is required");
        Object value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(ArtemisProperty.of(key, value));
    }

    @Override
    public void remove(String key) {
        requireNonNull(key, "key is required");
        properties.remove(key);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultArtemisVertex)) {
            return false;
        }
        DefaultArtemisVertex that = (DefaultArtemisVertex) o;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(properties);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultArtemisVertex{");
        sb.append("properties=").append(properties);
        sb.append(", id=").append(id);
        sb.append(", label='").append(label).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
