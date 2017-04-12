/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.couchbase.document;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Statement;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.couchbase.document.CouchbaseDocumentCollectionManagerAsync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.when;

@RunWith(WeldJUnit4Runner.class)
public class DefaultCouchbaseDocumentRepositoryAsyncTest {

    @Inject
    private DocumentEntityConverter converter;

    private CouchbaseDocumentCollectionManagerAsync managerAsync;

    private CouchbaseDocumentRepositoryAsync repositoryAsync;


    @Before
    public void setUp() {
        managerAsync = Mockito.mock(CouchbaseDocumentCollectionManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerAsync);

        repositoryAsync = new DefaultCouchbaseDocumentRepositoryAsync(converter, instance);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
    }


    @Test
    public void shouldFind() {
        String query = "select * from Person where name = ?";
        Consumer<List<Person>> callBack = p -> {
        };
        JsonObject params = JsonObject.create().put("name", "Ada");
        repositoryAsync.n1qlQuery(query, params, callBack);
        Mockito.verify(managerAsync).n1qlQuery(Mockito.eq(query), Mockito.eq(params), Mockito.any(Consumer.class));

    }

    @Test
    public void shouldFindStatement() {
        Statement query = Mockito.mock(Statement.class);
        Consumer<List<Person>> callBack = p -> {
        };
        JsonObject params = JsonObject.create().put("name", "Ada");
        repositoryAsync.n1qlQuery(query, params, callBack);
        Mockito.verify(managerAsync).n1qlQuery(Mockito.eq(query), Mockito.eq(params), Mockito.any(Consumer.class));
    }

    @Test
    public void shouldFind1() {
        String query = "select * from Person where name = ?";
        Consumer<List<Person>> callBack = p -> {
        };
        repositoryAsync.n1qlQuery(query, callBack);
        Mockito.verify(managerAsync).n1qlQuery(Mockito.eq(query),  Mockito.any(Consumer.class));

    }

    @Test
    public void shouldFindStatement1() {
        Statement query = Mockito.mock(Statement.class);
        Consumer<List<Person>> callBack = p -> {
        };
        repositoryAsync.n1qlQuery(query, callBack);
        Mockito.verify(managerAsync).n1qlQuery(Mockito.eq(query),  Mockito.any(Consumer.class));
    }


}