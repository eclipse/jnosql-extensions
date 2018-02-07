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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultArtemisVertexTest {


    @Test
    public void shouldRequireLabel() {
        assertThrows(NullPointerException.class, () -> new DefaultArtemisVertex(null));
    }

    @Test
    public void shouldCreateANewInstance() {
        ArtemisVertex vertex = new DefaultArtemisVertex("label");
        assertEquals("label", vertex.getLabel());
    }


    @Test
    public void shouldAddProperty() {
        ArtemisVertex vertex = new DefaultArtemisVertex("label");
        vertex.add("key", "value");
        Optional<Value> value = vertex.get("key");
        assertTrue(value.isPresent());
        assertEquals("value", value.get().get());

    }

    @Test
    public void shouldAddValueProperty() {
        ArtemisVertex vertex = new DefaultArtemisVertex("label");
        vertex.add("key", Value.of("value"));
        Optional<Value> value = vertex.get("key");
        assertTrue(value.isPresent());
        assertEquals("value", value.get().get());
    }

    @Test
    public void shouldListKeys() {
        ArtemisVertex vertex = new DefaultArtemisVertex("label");
        vertex.add("key", "value");
        vertex.add("key2", "value");
        vertex.add("key3", "value");
        assertThat(vertex.getKeys(), containsInAnyOrder("key", "key2", "key3"));
    }


}