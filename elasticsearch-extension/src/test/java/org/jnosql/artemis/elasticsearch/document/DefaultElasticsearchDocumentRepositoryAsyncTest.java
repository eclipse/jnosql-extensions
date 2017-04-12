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
package org.jnosql.artemis.elasticsearch.document;

import org.elasticsearch.index.query.QueryBuilder;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.elasticsearch.document.ElasticsearchDocumentCollectionManagerAsync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.mockito.Mockito.when;

@RunWith(WeldJUnit4Runner.class)
public class DefaultElasticsearchDocumentRepositoryAsyncTest {

    @Inject
    private DocumentEntityConverter converter;

    private ElasticsearchDocumentCollectionManagerAsync managerAsync;

    private ElasticsearchDocumentRepositoryAsync repositoryAsync;


    @Before
    public void setUp() {
        managerAsync = Mockito.mock(ElasticsearchDocumentCollectionManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerAsync);

        repositoryAsync = new DefaultElasticsearchDocumentRepositoryAsync(converter, instance);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
    }


    @Test
    public void shouldFind() {
        QueryBuilder queryBuilder = boolQuery().filter(termQuery("name", "Ada"));
        Consumer<List<Person>> callBack = p -> {};

        repositoryAsync.find(queryBuilder, callBack, "Person");

        Mockito.verify(managerAsync).find(Mockito.eq(queryBuilder), Mockito.any(Consumer.class),
                Mockito.eq("Person"));

    }
}