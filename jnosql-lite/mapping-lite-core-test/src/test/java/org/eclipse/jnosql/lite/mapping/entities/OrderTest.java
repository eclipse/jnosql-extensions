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
package org.eclipse.jnosql.lite.mapping.entities;

import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.lite.mapping.metadata.LiteEntitiesMetadata;
import org.eclipse.jnosql.mapping.metadata.ArrayFieldMetadata;
import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.metadata.MappingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderTest {


    private EntitiesMetadata mappings;

    private EntityMetadata entityMetadata;

    @BeforeEach
    public void setUp() {
        this.mappings = new LiteEntitiesMetadata();
        this.entityMetadata = this.mappings.get(Order.class);
    }

    @Test
    void shouldGetName() {
        Assertions.assertEquals("Order", entityMetadata.name());
    }

    @Test
    void shouldGetSimpleName() {
        Assertions.assertEquals(Order.class.getSimpleName(), entityMetadata.simpleName());
    }

    @Test
    void shouldGetClassName() {
        Assertions.assertEquals(Order.class.getSimpleName(), entityMetadata.simpleName());
    }

    @Test
    void shouldGetClassInstance() {
        Assertions.assertEquals(Order.class, entityMetadata.type());
    }

    @Test
    void shouldGetId() {
        Optional<FieldMetadata> id = this.entityMetadata.id();
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    void shouldCreateNewInstance() {
        Order order = entityMetadata.newInstance();
        org.assertj.core.api.Assertions.assertThat(order)
                .isNotNull().isInstanceOf(Order.class);
    }

    @Test
    void shouldGetFieldsName() {
        List<String> fields = entityMetadata.fieldsName();
        Assertions.assertEquals(3, fields.size());

        Assertions.assertTrue(fields.contains("id"));
        Assertions.assertTrue(fields.contains("users"));
        Assertions.assertTrue(fields.contains("products"));
    }

    @Test
    void shouldGetFieldsGroupByName() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        Assertions.assertNotNull(groupByName);
        Assertions.assertNotNull(groupByName.get("_id"));
        Assertions.assertNotNull(groupByName.get("users"));
    }

    @Test
    void shouldGetUsers() {
        var groupByName = this.entityMetadata.fieldsGroupByName();
        var users = groupByName.get("users");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(users).isNotNull();
            soft.assertThat(users).isInstanceOf(ArrayFieldMetadata.class);
            var arrayFieldMetadata = (ArrayFieldMetadata) users;
            soft.assertThat(arrayFieldMetadata.fieldName()).isEqualTo("users");
            soft.assertThat(arrayFieldMetadata.type()).isEqualTo(String[].class);
            soft.assertThat(arrayFieldMetadata.isEmbeddable()).isFalse();
            soft.assertThat(arrayFieldMetadata.elementType()).isEqualTo(String.class);
            soft.assertThat(arrayFieldMetadata.mappingType()).isEqualTo(MappingType.ARRAY);
        });
    }

    @Test
    void shouldGetProducts() {
        var groupByName = this.entityMetadata.fieldsGroupByName();
        var products = groupByName.get("products");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(products).isNotNull();
            soft.assertThat(products).isInstanceOf(ArrayFieldMetadata.class);
            var arrayFieldMetadata = (ArrayFieldMetadata) products;
            soft.assertThat(arrayFieldMetadata.fieldName()).isEqualTo("products");
            soft.assertThat(arrayFieldMetadata.type()).isEqualTo(Product[].class);
            soft.assertThat(arrayFieldMetadata.isEmbeddable()).isTrue();
            soft.assertThat(arrayFieldMetadata.elementType()).isEqualTo(Product.class);
            soft.assertThat(arrayFieldMetadata.mappingType()).isEqualTo(MappingType.ARRAY);
        });
    }
}
