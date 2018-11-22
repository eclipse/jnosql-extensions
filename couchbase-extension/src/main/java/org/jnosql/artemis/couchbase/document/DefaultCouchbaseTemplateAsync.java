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
package org.jnosql.artemis.couchbase.document;


import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Statement;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.AbstractDocumentTemplateAsync;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.reflection.ClassMappings;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.couchbase.document.CouchbaseDocumentCollectionManagerAsync;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * The default implementation of {@link CouchbaseTemplateAsync}
 */
@Typed(CouchbaseTemplateAsync.class)
class DefaultCouchbaseTemplateAsync extends AbstractDocumentTemplateAsync implements
        CouchbaseTemplateAsync {

    private DocumentEntityConverter converter;

    private Instance<CouchbaseDocumentCollectionManagerAsync> manager;

    private ClassMappings mappings;

    private Converters converters;

    @Inject
    DefaultCouchbaseTemplateAsync(DocumentEntityConverter converter,
                                  Instance<CouchbaseDocumentCollectionManagerAsync> manager,
                                  ClassMappings mappings, Converters converters) {
        this.converter = converter;
        this.manager = manager;
        this.mappings = mappings;
        this.converters = converters;
    }

    DefaultCouchbaseTemplateAsync() {
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
    public <T> void n1qlQuery(String n1qlQuery, JsonObject params, Consumer<List<T>> callback) {

        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(params, "params is required");
        Objects.requireNonNull(callback, "callback is required");
        Consumer<List<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().n1qlQuery(n1qlQuery, params, dianaCallBack);

    }

    @Override
    public <T> void n1qlQuery(Statement n1qlQuery, JsonObject params, Consumer<List<T>> callback) {
        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(params, "params is required");
        Objects.requireNonNull(callback, "callback is required");
        Consumer<List<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().n1qlQuery(n1qlQuery, params, dianaCallBack);
    }

    @Override
    public <T> void n1qlQuery(String n1qlQuery, Consumer<List<T>> callback) {

        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(callback, "callback is required");

        Consumer<List<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().n1qlQuery(n1qlQuery, dianaCallBack);

    }

    @Override
    public <T> void n1qlQuery(Statement n1qlQuery, Consumer<List<T>> callback) {
        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(callback, "callback is required");

        Consumer<List<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().n1qlQuery(n1qlQuery, dianaCallBack);
    }
}
