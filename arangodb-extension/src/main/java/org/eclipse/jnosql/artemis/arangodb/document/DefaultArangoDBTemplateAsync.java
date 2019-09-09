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
package org.eclipse.jnosql.artemis.arangodb.document;


import jakarta.nosql.document.DocumentCollectionManagerAsync;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.reflection.ClassMappings;
import org.eclipse.jnosql.artemis.document.AbstractDocumentTemplateAsync;
import org.eclipse.jnosql.diana.arangodb.document.ArangoDBDocumentCollectionManagerAsync;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of {@link ArangoDBTemplateAsync}
 */
@Typed(ArangoDBTemplateAsync.class)
class DefaultArangoDBTemplateAsync extends AbstractDocumentTemplateAsync implements
        ArangoDBTemplateAsync {

    private DocumentEntityConverter converter;

    private Instance<ArangoDBDocumentCollectionManagerAsync> manager;

    private ClassMappings mappings;

    private Converters converters;

    @Inject
    DefaultArangoDBTemplateAsync(DocumentEntityConverter converter,
                                 Instance<ArangoDBDocumentCollectionManagerAsync> manager,
                                 ClassMappings mappings,
                                 Converters converters) {
        this.converter = converter;
        this.manager = manager;
        this.mappings = mappings;
        this.converters = converters;
    }

    DefaultArangoDBTemplateAsync() {
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
    public <T> void aql(String query, Map<String, Object> values, Consumer<Stream<T>> callBack) {

        requireNonNull(query, "query is required");
        requireNonNull(values, "values is required");
        requireNonNull(callBack, "callback is required");

        Consumer<Stream<DocumentEntity>> dianaCallBack = d -> callBack.accept(
                d.map(getConverter()::toEntity)
                        .map(o -> (T) o));
        manager.get().aql(query, values, dianaCallBack);
    }
}
