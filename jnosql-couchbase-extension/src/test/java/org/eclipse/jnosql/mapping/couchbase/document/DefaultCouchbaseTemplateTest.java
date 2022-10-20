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
package org.eclipse.jnosql.mapping.couchbase.document;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.search.SearchQuery;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;
import jakarta.nosql.tck.test.CDIExtension;
import org.eclipse.jnosql.communication.couchbase.document.CouchbaseDocumentCollectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@CDIExtension
public class DefaultCouchbaseTemplateTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private EntitiesMetadata entities;

    @Inject
    private Converters converters;

    private CouchbaseDocumentCollectionManager manager;

    private CouchbaseTemplate template;


    @BeforeEach
    public void setup() {
        manager = Mockito.mock(CouchbaseDocumentCollectionManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultCouchbaseTemplate(instance, converter, flow, persistManager, entities, converters);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("_id", "Ada"));
        entity.add(Document.of("age", 10));

        when(manager.search(any(SearchQuery.class))).thenReturn(Stream.of(entity));

    }

    @Test
    public void shouldFindN1ql() {
        JsonObject params = JsonObject.create().put("name", "Ada");
        template.n1qlQuery("select * from Person where name = $name", params);
        Mockito.verify(manager).n1qlQuery("select * from Person where name = $name", params);
    }

    @Test
    public void shouldFindN1ql2() {
        template.n1qlQuery("select * from Person where name = $name");
        Mockito.verify(manager).n1qlQuery("select * from Person where name = $name");
    }

    @Test
    public void shouldSearch() {
        SearchQuery query = Mockito.mock(SearchQuery.class);

        List<Person> people = template.<Person>search(query).collect(Collectors.toList());

        assertFalse(people.isEmpty());
        assertEquals(1, people.size());
        Person person = people.get(0);

        assertEquals("Ada", person.getName());
    }

}