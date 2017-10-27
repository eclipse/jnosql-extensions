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
public class DefaultArangoDBTemplateAsyncTest {

    @Inject
    private DocumentEntityConverter converter;

    private CouchbaseDocumentCollectionManagerAsync managerAsync;

    private ArangoDBTemplateAsync templateAsync;


    @Before
    public void setUp() {
        managerAsync = Mockito.mock(CouchbaseDocumentCollectionManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerAsync);

        templateAsync = new DefaultArangoDBTemplateAsync(converter, instance);

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
        templateAsync.n1qlQuery(query, params, callBack);
        Mockito.verify(managerAsync).n1qlQuery(Mockito.eq(query), Mockito.eq(params), Mockito.any(Consumer.class));

    }

    @Test
    public void shouldFindStatement() {
        Statement query = Mockito.mock(Statement.class);
        Consumer<List<Person>> callBack = p -> {
        };
        JsonObject params = JsonObject.create().put("name", "Ada");
        templateAsync.n1qlQuery(query, params, callBack);
        Mockito.verify(managerAsync).n1qlQuery(Mockito.eq(query), Mockito.eq(params), Mockito.any(Consumer.class));
    }

    @Test
    public void shouldFind1() {
        String query = "select * from Person where name = ?";
        Consumer<List<Person>> callBack = p -> {
        };
        templateAsync.n1qlQuery(query, callBack);
        Mockito.verify(managerAsync).n1qlQuery(Mockito.eq(query),  Mockito.any(Consumer.class));

    }

    @Test
    public void shouldFindStatement1() {
        Statement query = Mockito.mock(Statement.class);
        Consumer<List<Person>> callBack = p -> {
        };
        templateAsync.n1qlQuery(query, callBack);
        Mockito.verify(managerAsync).n1qlQuery(Mockito.eq(query),  Mockito.any(Consumer.class));
    }


}