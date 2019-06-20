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
package org.jnosql.artemis.elasticsearch.document;

import org.elasticsearch.index.query.QueryBuilder;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.reflection.ClassMappings;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import org.jnosql.diana.elasticsearch.document.ElasticsearchDocumentCollectionManagerAsync;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(CDIExtension.class)
public class DefaultElasticsearchTemplateAsyncTest {

    @Inject
    private DocumentEntityConverter converter;

    private ElasticsearchDocumentCollectionManagerAsync managerAsync;

    private DefaultElasticsearchTemplateAsync templateAsync;

    @Inject
    private ClassMappings mappings;

    @Inject
    private Converters converters;


    @BeforeEach
    public void setUp() {
        managerAsync = Mockito.mock(ElasticsearchDocumentCollectionManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerAsync);

        templateAsync = new DefaultElasticsearchTemplateAsync(converter, instance, mappings, converters);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
    }


    @Test
    public void shouldFind() {
        QueryBuilder queryBuilder = boolQuery().filter(termQuery("name", "Ada"));
        Consumer<List<Person>> callBack = p -> {};

        templateAsync.search(queryBuilder, callBack, "Person");

        Mockito.verify(managerAsync).search(Mockito.eq(queryBuilder), Mockito.any(Consumer.class),
                Mockito.eq("Person"));

    }

    @Test
    public void shouldGetConverter() {
        assertNotNull(templateAsync.getConverter());
        assertEquals(converter, templateAsync.getConverter());
    }

    @Test
    public void shouldGetManager() {
        assertNotNull(templateAsync.getManager());
        assertEquals(managerAsync, templateAsync.getManager());
    }

    @Test
    public void shouldGetClassMapping() {
        assertNotNull(templateAsync.getClassMappings());
        assertEquals(mappings, templateAsync.getClassMappings());
    }

    @Test
    public void shouldGetConverters() {
        assertNotNull(templateAsync.getConverters());
        assertEquals(converters, templateAsync.getConverters());
    }
}