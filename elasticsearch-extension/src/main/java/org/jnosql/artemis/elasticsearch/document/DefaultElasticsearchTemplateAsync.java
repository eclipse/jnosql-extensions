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
package org.jnosql.artemis.elasticsearch.document;


import org.elasticsearch.index.query.QueryBuilder;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.AbstractDocumentTemplateAsync;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.reflection.ClassMappings;
import jakarta.nosql.document.DocumentCollectionManagerAsync;
import jakarta.nosql.document.DocumentEntity;
import org.jnosql.diana.elasticsearch.document.ElasticsearchDocumentCollectionManagerAsync;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * The default implementation of {@link ElasticsearchTemplateAsync}
 */
@Typed(ElasticsearchTemplateAsync.class)
class DefaultElasticsearchTemplateAsync extends AbstractDocumentTemplateAsync implements
        ElasticsearchTemplateAsync {

    private DocumentEntityConverter converter;

    private Instance<ElasticsearchDocumentCollectionManagerAsync> manager;

    private ClassMappings mappings;

    private Converters converters;

    @Inject
    DefaultElasticsearchTemplateAsync(DocumentEntityConverter converter,
                                      Instance<ElasticsearchDocumentCollectionManagerAsync> manager,
                                      ClassMappings mappings, Converters converters) {
        this.converter = converter;
        this.manager = manager;
        this.mappings = mappings;
        this.converters = converters;
    }

    DefaultElasticsearchTemplateAsync() {
    }

    @Override
    protected DocumentEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected DocumentCollectionManagerAsync getManager() {
        return manager.get();
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
    public <T> void search(QueryBuilder query, Consumer<List<T>> callBack, String... types) {
        Objects.requireNonNull(query, "query is required");
        Objects.requireNonNull(callBack, "callBack is required");

        Consumer<List<DocumentEntity>> dianaCallBack = d -> callBack.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().search(query, dianaCallBack, types);
    }
}
