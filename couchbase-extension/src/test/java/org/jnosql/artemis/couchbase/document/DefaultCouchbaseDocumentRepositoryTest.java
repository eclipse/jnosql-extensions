/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
public class DefaultCouchbaseDocumentRepositoryTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    private CouchbaseDocumentCollectionManager manager;

    private CouchbaseDocumentRepository repository;


    @Before
    public void setup() {
        manager = Mockito.mock(CouchbaseDocumentCollectionManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        repository = new DefaultCouchbaseDocumentRepository(instance, converter, flow, persistManager);

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