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
import org.jnosql.artemis.document.AbstractDocumentRepository;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.couchbase.document.CouchbaseDocumentCollectionManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Default implementation of {@link CouchbaseDocumentRepository}
 */
class DefaultCouchbaseDocumentRepository extends AbstractDocumentRepository
        implements CouchbaseDocumentRepository {

    private Instance<CouchbaseDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    @Inject
    DefaultCouchbaseDocumentRepository(Instance<CouchbaseDocumentCollectionManager> manager,
                                       DocumentEntityConverter converter, DocumentWorkflow flow,
                                       DocumentEventPersistManager persistManager) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
    }

    DefaultCouchbaseDocumentRepository() {
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
    public <T> List<T> n1qlQuery(String n1qlQuery, JsonObject params) throws NullPointerException {
        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(params, "params is required");
        return null;
    }

    @Override
    public <T> List<T> n1qlQuery(Statement n1qlQuery, JsonObject params) throws NullPointerException {
        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        Objects.requireNonNull(params, "params is required");
        return manager.get().n1qlQuery(n1qlQuery, params).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> n1qlQuery(String n1qlQuery) throws NullPointerException {
        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        return manager.get().n1qlQuery(n1qlQuery).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> n1qlQuery(Statement n1qlQuery) throws NullPointerException {
        Objects.requireNonNull(n1qlQuery, "n1qlQuery is required");
        return manager.get().n1qlQuery(n1qlQuery).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }
}
