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
package org.jnosql.artemis.couchbase.document;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Statement;
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

import static org.mockito.Mockito.when;


@RunWith(WeldJUnit4Runner.class)
public class DefaultCouchbaseTemplateTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    private CouchbaseDocumentCollectionManager manager;

    private CouchbaseTemplate repository;


    @Before
    public void setup() {
        manager = Mockito.mock(CouchbaseDocumentCollectionManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        repository = new DefaultCouchbaseTemplate(instance, converter, flow, persistManager);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));

    }

    @Test
    public void shouldFindN1ql() {
        JsonObject params = JsonObject.create().put("name", "Ada");
        repository.n1qlQuery("select * from Person where name = $name", params);
        Mockito.verify(manager).n1qlQuery("select * from Person where name = $name", params);
    }

    @Test
    public void shouldFindN1qlStatment() {
        Statement statement = Mockito.mock(Statement.class);
        JsonObject params = JsonObject.create().put("name", "Ada");
        repository.n1qlQuery(statement, params);
        Mockito.verify(manager).n1qlQuery(statement, params);
    }


    @Test
    public void shouldFindN1ql2() {
        repository.n1qlQuery("select * from Person where name = $name");
        Mockito.verify(manager).n1qlQuery("select * from Person where name = $name");
    }

    @Test
    public void shouldFindN1qlStatment2() {
        Statement statement = Mockito.mock(Statement.class);
        repository.n1qlQuery(statement);
        Mockito.verify(manager).n1qlQuery(statement);
    }

}