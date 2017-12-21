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


import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.AbstractDocumentTemplateAsync;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.arangodb.document.ArangoDBDocumentCollectionManagerAsync;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * The default implementation of {@link ArangoDBTemplateAsync}
 */
@Typed(ArangoDBTemplateAsync.class)
class DefaultArangoDBTemplateAsync extends AbstractDocumentTemplateAsync implements
        ArangoDBTemplateAsync {

    private DocumentEntityConverter converter;

    private Instance<ArangoDBDocumentCollectionManagerAsync> manager;

    private ClassRepresentations classRepresentations;

    private Converters converters;

    @Inject
    DefaultArangoDBTemplateAsync(DocumentEntityConverter converter,
                                 Instance<ArangoDBDocumentCollectionManagerAsync> manager,
                                 ClassRepresentations classRepresentations,
                                 Converters converters) {
        this.converter = converter;
        this.manager = manager;
        this.classRepresentations = classRepresentations;
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
    protected ClassRepresentations getClassRepresentations() {
        return classRepresentations;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }


    @Override
    public <T> void aql(String query, Map<String, Object> values, Consumer<List<T>> callBack)
            throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {

        requireNonNull(query, "query is required");
        requireNonNull(values, "values is required");
        requireNonNull(callBack, "callback is required");

        Consumer<List<DocumentEntity>> dianaCallBack = d -> callBack.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().aql(query, values, dianaCallBack);
    }
}
