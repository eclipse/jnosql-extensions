/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.couchbase.document;


import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Statement;
import org.jnosql.artemis.document.AbstractDocumentRepositoryAsync;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.couchbase.document.CouchbaseDocumentCollectionManagerAsync;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * The default implementation of {@link CouchbaseDocumentRepositoryAsync}
 */
class DefaultCouchbaseDocumentRepositoryAsync extends AbstractDocumentRepositoryAsync implements
        CouchbaseDocumentRepositoryAsync {

    private DocumentEntityConverter converter;

    private Instance<CouchbaseDocumentCollectionManagerAsync> manager;

    @Inject
    DefaultCouchbaseDocumentRepositoryAsync(DocumentEntityConverter converter,
                                            Instance<CouchbaseDocumentCollectionManagerAsync> manager) {
        this.converter = converter;
        this.manager = manager;
    }

    DefaultCouchbaseDocumentRepositoryAsync() {
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
    public <T> void n1qlQuery(String n1qlQuery, JsonObject params, Consumer<List<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {

        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(params, "params is required");
        Objects.requireNonNull(callback, "callback is required");
        Consumer<List<DocumentEntity>> dianaCallBack = d -> {
            callback.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };
        manager.get().n1qlQuery(n1qlQuery, params, dianaCallBack);

    }

    @Override
    public <T> void n1qlQuery(Statement n1qlQuery, JsonObject params, Consumer<List<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {
        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(params, "params is required");
        Objects.requireNonNull(callback, "callback is required");
        Consumer<List<DocumentEntity>> dianaCallBack = d -> {
            callback.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };
        manager.get().n1qlQuery(n1qlQuery, params, dianaCallBack);
    }

    @Override
    public <T> void n1qlQuery(String n1qlQuery, Consumer<List<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {

        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(callback, "callback is required");

        Consumer<List<DocumentEntity>> dianaCallBack = d -> {
            callback.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };
        manager.get().n1qlQuery(n1qlQuery, dianaCallBack);

    }

    @Override
    public <T> void n1qlQuery(Statement n1qlQuery, Consumer<List<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {
        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(callback, "callback is required");

        Consumer<List<DocumentEntity>> dianaCallBack = d -> {
            callback.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };
        manager.get().n1qlQuery(n1qlQuery, dianaCallBack);
    }
}
