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
package org.eclipse.jnosql.mapping.elasticsearch.document;


import jakarta.nosql.document.DocumentCollectionManager;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import org.eclipse.jnosql.artemis.reflection.ClassMappings;
import org.elasticsearch.index.query.QueryBuilder;
import org.eclipse.jnosql.artemis.document.AbstractDocumentTemplate;
import org.eclipse.jnosql.diana.elasticsearch.document.ElasticsearchDocumentCollectionManager;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The Default implementation of {@link ElasticsearchTemplate}
 */

@Typed(ElasticsearchTemplate.class)
class DefaultElasticsearchTemplate extends AbstractDocumentTemplate
        implements ElasticsearchTemplate {

    private Instance<ElasticsearchDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    private ClassMappings mappings;

    private Converters converters;

    @Inject
    DefaultElasticsearchTemplate(Instance<ElasticsearchDocumentCollectionManager> manager,
                                 DocumentEntityConverter converter, DocumentWorkflow flow,
                                 DocumentEventPersistManager persistManager,
                                 ClassMappings mappings,
                                 Converters converters) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
        this.mappings = mappings;
        this.converters = converters;
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
    protected ClassMappings getClassMappings() {
        return mappings;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @Override
    public <T> Stream<T> search(QueryBuilder query) {
        Objects.requireNonNull(query, "query is required");
        Stream<DocumentEntity> entities = manager.get().search(query);
        return entities.map(converter::toEntity).map(e -> (T) e);
    }
}
