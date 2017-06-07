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
import org.jnosql.artemis.document.AbstractDocumentTemplate;
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
 * The Default implementation of {@link CouchbaseTemplate}
 */
class DefaultCouchbaseTemplate extends AbstractDocumentTemplate
        implements CouchbaseTemplate {

    private Instance<CouchbaseDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    @Inject
    DefaultCouchbaseTemplate(Instance<CouchbaseDocumentCollectionManager> manager,
                             DocumentEntityConverter converter, DocumentWorkflow flow,
                             DocumentEventPersistManager persistManager) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
    }

    DefaultCouchbaseTemplate() {
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
        return manager.get().n1qlQuery(n1qlQuery, params).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
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
