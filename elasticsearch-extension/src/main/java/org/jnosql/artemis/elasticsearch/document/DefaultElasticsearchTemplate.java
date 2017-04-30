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
import org.jnosql.artemis.document.AbstractDocumentTemplate;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.elasticsearch.document.ElasticsearchDocumentCollectionManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Default implementation of {@link ElasticsearchTemplate}
 */
class DefaultElasticsearchTemplate extends AbstractDocumentTemplate
        implements ElasticsearchTemplate {

    private Instance<ElasticsearchDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    @Inject
    DefaultElasticsearchTemplate(Instance<ElasticsearchDocumentCollectionManager> manager,
                                 DocumentEntityConverter converter, DocumentWorkflow flow,
                                 DocumentEventPersistManager persistManager) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
    }

    DefaultElasticsearchTemplate() {
    }

    @Override
    protected DocumentEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected DocumentCollectionManager getManager() {
        return manager.get();
    }

    @Override
    protected DocumentWorkflow getWorkflow() {
        return flow;
    }

    @Override
    protected DocumentEventPersistManager getPersistManager() {
        return persistManager;
    }

    @Override
    public <T> List<T> find(QueryBuilder query, String... types) {
        Objects.requireNonNull(query, "query is required");
        List<DocumentEntity> entities = manager.get().find(query, types);
        return entities.stream().map(converter::toEntity).map(e -> (T) e)
                .collect(Collectors.toList());
    }
}
