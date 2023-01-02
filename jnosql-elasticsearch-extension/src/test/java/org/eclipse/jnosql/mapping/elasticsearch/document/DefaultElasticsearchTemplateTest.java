/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.mapping.elasticsearch.document;

import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import jakarta.nosql.tck.test.CDIExtension;
import org.eclipse.jnosql.communication.elasticsearch.document.ElasticsearchDocumentManager;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@CDIExtension
public class DefaultElasticsearchTemplateTest {

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

    private ElasticsearchDocumentManager manager;

    private DefaultElasticsearchTemplate template;


    @BeforeEach
    public void setup() {
        manager = Mockito.mock(ElasticsearchDocumentManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultElasticsearchTemplate(instance, converter, flow, persistManager, entities, converters);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
        when(manager.search(Mockito.any(QueryBuilder.class)))
                .thenReturn(Stream.of(entity));
    }

    @Test
    public void shouldFindQuery() {
        QueryBuilder queryBuilder = boolQuery().filter(termQuery("name", "Ada"));
        List<Person> people = template.<Person>search(queryBuilder).collect(Collectors.toList());

        assertThat(people).contains(new Person("Ada", 10));
        Mockito.verify(manager).search(Mockito.eq(queryBuilder));
    }

    @Test
    public void shouldGetConverter() {
        assertNotNull(template.getConverter());
        assertEquals(converter, template.getConverter());
    }

    @Test
    public void shouldGetManager() {
        assertNotNull(template.getManager());
        assertEquals(manager, template.getManager());
    }


    @Test
    public void shouldGetWorkflow() {
        assertNotNull(template.getWorkflow());
        assertEquals(flow, template.getWorkflow());
    }

    @Test
    public void shouldGetPersistManager() {
        assertNotNull(template.getEventManager());
        assertEquals(persistManager, template.getEventManager());
    }


    @Test
    public void shouldGetClassMappings() {
        assertNotNull(template.getEntities());
        assertEquals(entities, template.getEntities());
    }

    @Test
    public void shouldGetConverters() {
        assertNotNull(template.getConverters());
        assertEquals(converters, template.getConverters());
    }
}