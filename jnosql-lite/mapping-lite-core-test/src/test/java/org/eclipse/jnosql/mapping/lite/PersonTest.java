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

import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.lite.mapping.LiteEntitiesMetadata;
import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.metadata.GenericFieldMetadata;
import org.eclipse.jnosql.mapping.metadata.MappingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonTest {


    private EntitiesMetadata mappings;

    private EntityMetadata entityMetadata;

    @BeforeEach
    public void setUp() {
        this.mappings = new LiteEntitiesMetadata();
        this.entityMetadata = this.mappings.get(Person.class);
    }

    @Test
    public void shouldGetName() {
        Assertions.assertEquals("Person", entityMetadata.name());
    }

    @Test
    public void shouldGetSimpleName() {
        Assertions.assertEquals(Person.class.getSimpleName(), entityMetadata.simpleName());
    }

    @Test
    public void shouldGetClassName() {
        Assertions.assertEquals(Person.class.getSimpleName(), entityMetadata.simpleName());
    }

    @Test
    public void shouldGetClassInstance() {
        Assertions.assertEquals(Person.class, entityMetadata.type());
    }

    @Test
    public void shouldGetId() {
        Optional<FieldMetadata> id = this.entityMetadata.id();
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    public void shouldCreateNewInstance() {
        Person person = entityMetadata.newInstance();
        org.assertj.core.api.Assertions.assertThat(person)
                .isNotNull().isInstanceOf(Person.class);
    }

    @Test
    public void shouldGetFieldsName() {
        List<String> fields = entityMetadata.fieldsName();
        Assertions.assertEquals(5, fields.size());
        Assertions.assertTrue(fields.contains("id"));
        Assertions.assertTrue(fields.contains("username"));
        Assertions.assertTrue(fields.contains("email"));
        Assertions.assertTrue(fields.contains("contacts"));
        Assertions.assertTrue(fields.contains("pet"));
    }

    @Test
    public void shouldGetFieldsGroupByName() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        Assertions.assertNotNull(groupByName);
        Assertions.assertNotNull(groupByName.get("_id"));
        Assertions.assertNotNull(groupByName.get("native"));
        Assertions.assertNotNull(groupByName.get("email"));
        Assertions.assertNotNull(groupByName.get("contacts"));
        Assertions.assertNotNull(groupByName.get("pet"));
    }

    @Test
    public void shouldGetter() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        Person person = new Person();
        person.setId(1L);
        person.setUsername("otaviojava");
        person.setEmail("otavio@java.com");
        person.setContacts(List.of("Poliana", "Maria"));
        Animal ada = new Animal();
        ada.setName("Ada");
        ada.setColor("black");
        person.setPet(ada);

        FieldMetadata id = groupByName.get("_id");
        FieldMetadata username = groupByName.get("native");
        FieldMetadata email = groupByName.get("email");
        FieldMetadata contacts = groupByName.get("contacts");
        FieldMetadata pet = groupByName.get("pet");

        Assertions.assertEquals(1L, id.read(person));
        Assertions.assertEquals("otaviojava", username.read(person));
        Assertions.assertEquals("otavio@java.com", email.read(person));
        Assertions.assertEquals(List.of("Poliana", "Maria"), contacts.read(person));
        Assertions.assertEquals(ada, pet.read(person));
    }

    @Test
    public void shouldSetter() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        Person person = new Person();
        Animal ada = new Animal();
        ada.setName("Ada");
        ada.setColor("black");

        FieldMetadata id = groupByName.get("_id");
        FieldMetadata username = groupByName.get("native");
        FieldMetadata email = groupByName.get("email");
        FieldMetadata contacts = groupByName.get("contacts");
        FieldMetadata pet = groupByName.get("pet");

        id.write(person, 1L);
        username.write(person, "otaviojava");
        email.write(person, "otavio@java.com");
        contacts.write(person, List.of("Poliana", "Maria"));
        pet.write(person, ada);

        Assertions.assertEquals(1L, id.read(person));
        Assertions.assertEquals("otaviojava", username.read(person));
        Assertions.assertEquals("otavio@java.com", email.read(person));
        Assertions.assertEquals(List.of("Poliana", "Maria"), contacts.read(person));
        Assertions.assertEquals(ada, pet.read(person));
    }

    @Test
    public void shouldReturnGenerics() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        FieldMetadata contacts = groupByName.get("contacts");
        GenericFieldMetadata genericFieldMetadata = (GenericFieldMetadata) contacts;
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(genericFieldMetadata.elementType()).isEqualTo(String.class);
            soft.assertThat(genericFieldMetadata.collectionInstance()).isInstanceOf(List.class);
        });
    }

    @Test
    public void shouldReadEntityField(){
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        FieldMetadata pet = groupByName.get("pet");
        assertThat(pet.isId()).isFalse();
        assertThat(pet.mappingType()).isEqualTo(MappingType.ENTITY);
    }

    @Test
    public void shouldGetCustomAnnotation() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        FieldMetadata email = groupByName.get("email");
        Optional<String> value = email.value(CustomAnnotation.class);
        assertThat(value).isNotEmpty().get().isEqualTo("email");
    }
}
