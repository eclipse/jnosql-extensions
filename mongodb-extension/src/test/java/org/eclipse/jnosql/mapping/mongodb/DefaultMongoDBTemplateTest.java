/*
 *  Copyright (c) 2022 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.mongodb;

import com.mongodb.client.model.Filters;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import org.bson.conversions.Bson;
import org.eclipse.jnosql.communication.mongodb.document.MongoDBDocumentCollectionManager;
import org.eclipse.jnosql.mapping.reflection.ClassMappings;
import org.eclipse.jnosql.mapping.test.CDIExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@CDIExtension
class DefaultMongoDBTemplateTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private ClassMappings mappings;

    @Inject
    private Converters converters;

    private MongoDBTemplate template;

    private MongoDBDocumentCollectionManager manager;

    @BeforeEach
    public void setUp() {
        this.manager = mock(MongoDBDocumentCollectionManager.class);
        Instance instance = mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultMongoDBTemplate(instance, converter, flow, mappings, converters, persistManager);
    }

    @Test
    public void shouldReturnErrorOnDeleteMethod() {
        assertThrows(NullPointerException.class, () -> template.delete((String) null, null));
        assertThrows(NullPointerException.class, () -> template.delete("Collection", null));
        assertThrows(NullPointerException.class, () -> template.delete((String) null,
                eq("name", "Poliana")));

        assertThrows(NullPointerException.class, () -> template.delete(Person.class, null));
        assertThrows(NullPointerException.class, () -> template.delete((Class<Object>) null,
                eq("name", "Poliana")));
    }

    @Test
    public void shouldDeleteWithCollectionName() {
        Bson filter = eq("name", "Poliana");
        template.delete("Person", filter);
        Mockito.verify(manager).delete("Person", filter);
    }

    @Test
    public void shouldDeleteWithEntity() {
        Bson filter = eq("name", "Poliana");
        template.delete(Person.class, filter);
        Mockito.verify(manager).delete("Person", filter);
    }

    @Test
    public void shouldReturnErrorOnSelectMethod() {
        assertThrows(NullPointerException.class, () -> template.select((String) null, null));
        assertThrows(NullPointerException.class, () -> template.select("Collection", null));
        assertThrows(NullPointerException.class, () -> template.select((String) null,
                eq("name", "Poliana")));

        assertThrows(NullPointerException.class, () -> template.select(Person.class, null));
        assertThrows(NullPointerException.class, () -> template.select((Class<Object>) null,
                eq("name", "Poliana")));
    }

    @Test
    public void shouldSelectWithCollectionName() {
        DocumentEntity entity = DocumentEntity.of("Person", Arrays
                .asList(Document.of("_id", "Poliana"),
                        Document.of("age", 30)));
        Bson filter = eq("name", "Poliana");
        Mockito.when(manager.select("Person", filter))
                .thenReturn(Stream.of(entity));
        Stream<Person> stream = template.select("Person", filter);
        Assertions.assertNotNull(stream);
        Person poliana = stream.findFirst()
                .orElseThrow(() -> new IllegalStateException("There is an issue on the test"));

        Assertions.assertNotNull(poliana);
        assertEquals("Poliana", poliana.getName());
        assertEquals(30, poliana.getAge());
    }

    @Test
    public void shouldSelectWithEntity() {
        DocumentEntity entity = DocumentEntity.of("Person", Arrays
                .asList(Document.of("_id", "Poliana"),
                        Document.of("age", 30)));
        Bson filter = eq("name", "Poliana");
        Mockito.when(manager.select("Person", filter))
                .thenReturn(Stream.of(entity));
        Stream<Person> stream = template.select(Person.class, filter);
        Assertions.assertNotNull(stream);
        Person poliana = stream.findFirst()
                .orElseThrow(() -> new IllegalStateException("There is an issue on the test"));

        Assertions.assertNotNull(poliana);
        assertEquals("Poliana", poliana.getName());
        assertEquals(30, poliana.getAge());
    }

}