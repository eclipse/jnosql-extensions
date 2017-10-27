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

import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.arangodb.document.ArangoDBDocumentCollectionManagerAsync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.Mockito.when;

@RunWith(WeldJUnit4Runner.class)
public class DefaultArangoDBTemplateAsyncTest {

    @Inject
    private DocumentEntityConverter converter;

    private ArangoDBDocumentCollectionManagerAsync managerAsync;

    private ArangoDBTemplateAsync templateAsync;


    @Before
    public void setUp() {
        managerAsync = Mockito.mock(ArangoDBDocumentCollectionManagerAsync.class);
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
        Map<String, Object> params = Collections.singletonMap("name", "Ada");
        templateAsync.aql(query, params, callBack);
        Mockito.verify(managerAsync).aql(Mockito.eq(query), Mockito.eq(params), Mockito.any(Consumer.class));

    }


}