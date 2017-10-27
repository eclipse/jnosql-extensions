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
package org.jnosql.artemis.arangodb.document;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.search.SearchQuery;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.couchbase.document.CouchbaseDocumentCollectionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(WeldJUnit4Runner.class)
public class DefaultArangoDBTemplateTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    private CouchbaseDocumentCollectionManager manager;

    private ArangoDBTemplate template;


    @Before
    public void setup() {
        manager = Mockito.mock(CouchbaseDocumentCollectionManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultArangoDBTemplate(instance, converter, flow, persistManager);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("_id", "Ada"));
        entity.add(Document.of("age", 10));

        when(manager.search(any(SearchQuery.class))).thenReturn(singletonList(entity));

    }

    @Test
    public void shouldFindN1ql() {
        JsonObject params = JsonObject.create().put("name", "Ada");
        template.n1qlQuery("select * from Person where name = $name", params);
        Mockito.verify(manager).n1qlQuery("select * from Person where name = $name", params);
    }

    @Test
    public void shouldFindN1qlStatment() {
        Statement statement = Mockito.mock(Statement.class);
        JsonObject params = JsonObject.create().put("name", "Ada");
        template.n1qlQuery(statement, params);
        Mockito.verify(manager).n1qlQuery(statement, params);
    }


    @Test
    public void shouldFindN1ql2() {
        template.n1qlQuery("select * from Person where name = $name");
        Mockito.verify(manager).n1qlQuery("select * from Person where name = $name");
    }

    @Test
    public void shouldFindN1qlStatment2() {
        Statement statement = Mockito.mock(Statement.class);
        template.n1qlQuery(statement);
        Mockito.verify(manager).n1qlQuery(statement);
    }

    @Test
    public void shouldSearch() {
        SearchQuery query = Mockito.mock(SearchQuery.class);

        List<Person> people = template.search(query);

        assertFalse(people.isEmpty());
        assertEquals(1, people.size());
        Person person = people.get(0);

        assertEquals("Ada", person.getName());
    }

}