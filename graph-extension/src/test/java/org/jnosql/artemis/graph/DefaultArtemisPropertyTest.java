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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DefaultArtemisPropertyTest {


    @Test
    public void shouldReturnErrorWhenKeyINull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ArtemisProperty.of(null, 10L);
        });
    }

    @Test
    public void shouldReturnErrorWhenValueINull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ArtemisProperty.of("key", null);
        });
    }

    @Test
    public void shouldReturnKey() {
        ArtemisProperty element = ArtemisProperty.of("key", 10L);
        assertEquals("key", element.getKey());
    }

    @Test
    public void shouldReturnValue() {
        ArtemisProperty element = ArtemisProperty.of("key", 10L);
        assertEquals(Value.of(10L), element.getValue());
    }

    @Test
    public void shouldReturnValueAsObject() {
        long value = 10L;
        ArtemisProperty element = ArtemisProperty.of("key", value);
        assertEquals(value, element.get());
    }


    @Test
    public void shouldCreateInstanceValue() {
        ArtemisProperty element = ArtemisProperty.of("key", Value.of(10L));
        assertEquals(Value.of(10L), element.getValue());
        assertEquals("key", element.getKey());
        assertEquals(10L, element.get());
    }

}