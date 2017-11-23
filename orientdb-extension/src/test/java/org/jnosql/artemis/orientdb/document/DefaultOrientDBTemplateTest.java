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

import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.orientdb.document.OrientDBDocumentCollectionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.contains;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(CDIJUnitRunner.class)
public class DefaultOrientDBTemplateTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private ClassRepresentations classRepresentations;

    private OrientDBDocumentCollectionManager manager;

    private OrientDBTemplate template;


    @Before
    public void setup() {
        manager = Mockito.mock(OrientDBDocumentCollectionManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultOrientDBTemplate(instance, converter, flow, persistManager, classRepresentations);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
        when(manager.sql(Mockito.anyString(), Mockito.any(String[].class)))
                .thenReturn(Collections.singletonList(entity));
    }

    @Test
    public void shouldFindQuery() {
        List<Person> people = template.sql("sql * from Person where name = ?", "Ada");

        assertThat(people, contains(new Person("Ada", 10)));
        verify(manager).sql(Mockito.eq("sql * from Person where name = ?"), Mockito.eq("Ada"));
    }

    @Test
    public void shouldLive() {

        DocumentQuery query = select().from("Person").build();
        
        Consumer<Person> callBack = p -> {
        };
        template.live(query, callBack);
        verify(manager).live(Mockito.eq(query), Mockito.any(Consumer.class));
    }

    @Test
    public void shouldLiveQuery() {
        Consumer<Person> callBack = p -> {
        };
        template.live("sql from Person where name = ?", callBack, "Ada");
        verify(manager).live(Mockito.eq("sql from Person where name = ?"),
                Mockito.any(Consumer.class), Mockito.eq("Ada"));
    }
}