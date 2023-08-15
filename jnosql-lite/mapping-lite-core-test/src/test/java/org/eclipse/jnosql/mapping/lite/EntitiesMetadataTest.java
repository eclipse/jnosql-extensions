/*
 *  Copyright (c) 2020 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.lite;

import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.LiteEntitiesMetadata;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntitiesMetadataTest {

    private EntitiesMetadata mappings;

    @BeforeEach
    public void setUp() {
        this.mappings = new LiteEntitiesMetadata();
    }

    @Test
    public void shouldReturnNPEWhenIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> this.mappings.get(null));
    }

    @Test
    public void shouldReturnFromClass() {
        EntityMetadata entityMetadata = this.mappings.get(Animal.class);
        Assertions.assertNotNull(entityMetadata);
        Assertions.assertEquals(Animal.class, entityMetadata.type());
        Assertions.assertEquals(Car.class, mappings.get(Car.class).type());
        Assertions.assertEquals(Person.class, mappings.get(Person.class).type());
    }

    @Test
    public void shouldReturnFromName() {
        Assertions.assertEquals(Animal.class, mappings.findByName("kind").type());
        Assertions.assertEquals(Car.class, mappings.findByName("car").type());
        Assertions.assertEquals(Person.class, mappings.findByName("Person").type());
    }
}
