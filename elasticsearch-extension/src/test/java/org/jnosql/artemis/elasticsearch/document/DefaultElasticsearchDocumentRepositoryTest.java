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
import org.elasticsearch.index.query.QueryBuilders;
import org.hamcrest.Matchers;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.elasticsearch.document.ElasticsearchDocumentCollectionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(WeldJUnit4Runner.class)
public class DefaultElasticsearchDocumentRepositoryTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    private ElasticsearchDocumentCollectionManager manager;

    private ElasticsearchDocumentRepository repository;


    @Before
    public void setup() {
        manager = Mockito.mock(ElasticsearchDocumentCollectionManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        repository = new DefaultElasticsearchDocumentRepository(instance, converter, flow, persistManager);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
        when(manager.find(Mockito.any(QueryBuilder.class), Mockito.any(String[].class)))
                .thenReturn(Collections.singletonList(entity));
    }

    @Test
    public void shouldFindQuery() {
        QueryBuilder queryBuilder = boolQuery().filter(termQuery("name", "Ada"));
        List<Person> people = repository.find(queryBuilder, "Person");

        assertThat(people, contains(new Person("Ada", 10)));
        Mockito.verify(manager).find(Mockito.eq(queryBuilder), Mockito.eq("Person"));

    }
}