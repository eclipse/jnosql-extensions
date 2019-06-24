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
package org.jnosql.artemis.orientdb.document;

import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.reflection.ClassMappings;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import org.jnosql.diana.orientdb.document.OrientDBDocumentCollectionManagerAsync;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.when;

@ExtendWith(CDIExtension.class)
public class DefaultOrientDBTemplateAsyncTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private ClassMappings mappings;

    @Inject
    private Converters converters;

    private OrientDBDocumentCollectionManagerAsync managerAsync;

    private OrientDBTemplateAsync templateAsync;


    @BeforeEach
    public void setUp() {
        managerAsync = Mockito.mock(OrientDBDocumentCollectionManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerAsync);

        templateAsync = new DefaultOrientDBTemplateAsync(converter, instance, mappings, converters);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
    }


    @Test
    public void shouldFind() {
        String query = "sql * from Person where name = ?";
        Consumer<List<Person>> callBack = p -> {};

        templateAsync.sql(query, callBack, "Person");

        Mockito.verify(managerAsync).sql(Mockito.eq(query), Mockito.any(Consumer.class),
                Mockito.eq("Person"));

    }
}