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
package org.jnosql.artemis.orientdb.document;


import org.jnosql.artemis.document.AbstractDocumentRepository;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.orientdb.document.OrientDBDocumentCollectionManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Default implementation of {@link OrientDBDocumentRepository}
 */
class DefaultOrientDBDocumentRepository extends AbstractDocumentRepository
        implements OrientDBDocumentRepository {

    private Instance<OrientDBDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    @Inject
    DefaultOrientDBDocumentRepository(Instance<OrientDBDocumentCollectionManager> manager,
                                      DocumentEntityConverter converter, DocumentWorkflow flow,
                                      DocumentEventPersistManager persistManager) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
    }

    DefaultOrientDBDocumentRepository() {
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
    public <T> List<T> find(String query, Object... params) throws NullPointerException {
        return manager.get().find(query, params).stream().map(converter::toEntity)
                .map(e -> (T) e)
                .collect(Collectors.toList());
    }

    @Override
    public <T> void live(DocumentQuery query, Consumer<T> callBack) throws NullPointerException {
        Objects.requireNonNull(query, "query is required");
        Objects.requireNonNull(callBack, "callBack is required");
        Function<DocumentEntity, T> function = e -> getConverter().toEntity(e);
        Consumer<DocumentEntity> dianaCallback = d -> {
            callBack.accept(converter.toEntity(d));
        };
        manager.get().live(query, dianaCallback);
    }

    @Override
    public <T> void live(String query, Consumer<T> callBack, Object... params) {
        Objects.requireNonNull(query, "query is required");
        Objects.requireNonNull(callBack, "callBack is required");
        Function<DocumentEntity, T> function = e -> getConverter().toEntity(e);
        Consumer<DocumentEntity> dianaCallback = d -> {
            callBack.accept(converter.toEntity(d));
        };
        manager.get().live(query, dianaCallback, params);
    }
}
