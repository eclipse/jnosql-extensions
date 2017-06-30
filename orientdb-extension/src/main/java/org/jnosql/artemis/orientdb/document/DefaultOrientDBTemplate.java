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
package org.jnosql.artemis.orientdb.document;


import org.jnosql.artemis.document.AbstractDocumentTemplate;
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
 * The Default implementation of {@link OrientDBTemplate}
 */
class DefaultOrientDBTemplate extends AbstractDocumentTemplate
        implements OrientDBTemplate {

    private Instance<OrientDBDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    @Inject
    DefaultOrientDBTemplate(Instance<OrientDBDocumentCollectionManager> manager,
                            DocumentEntityConverter converter, DocumentWorkflow flow,
                            DocumentEventPersistManager persistManager) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
    }

    DefaultOrientDBTemplate() {
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
    public <T> List<T> select(String query, Object... params) throws NullPointerException {
        return manager.get().sql(query, params).stream().map(converter::toEntity)
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
