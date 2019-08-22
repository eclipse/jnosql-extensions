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
import com.couchbase.client.java.search.SearchQuery;
import jakarta.nosql.document.DocumentCollectionManager;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import jakarta.nosql.mapping.reflection.ClassMappings;
import org.jnosql.artemis.document.AbstractDocumentTemplate;
import org.jnosql.diana.couchbase.document.CouchbaseDocumentCollectionManager;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The Default implementation of {@link CouchbaseTemplate}
 */
@Typed(CouchbaseTemplate.class)
class DefaultCouchbaseTemplate extends AbstractDocumentTemplate
        implements CouchbaseTemplate {

    private Instance<CouchbaseDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    private ClassMappings mappings;

    private Converters converters;

    @Inject
    DefaultCouchbaseTemplate(Instance<CouchbaseDocumentCollectionManager> manager,
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
    protected ClassMappings getClassMappings() {
        return mappings;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @Override
    public <T> Stream<T> n1qlQuery(String n1qlQuery, JsonObject params) {
        requireNonNull(n1qlQuery, "n1qlQuery is required");
        requireNonNull(params, "params is required");
        return manager.get().n1qlQuery(n1qlQuery, params)
                .map(converter::toEntity)
                .map(d -> (T) d);
    }

    @Override
    public <T> Stream<T> n1qlQuery(Statement n1qlQuery, JsonObject params) {
        requireNonNull(n1qlQuery, "n1qlQuery is required");
        requireNonNull(params, "params is required");
        return manager.get().n1qlQuery(n1qlQuery, params)
                .map(converter::toEntity)
                .map(d -> (T) d);
    }

    @Override
    public <T> Stream<T> n1qlQuery(String n1qlQuery) {
        requireNonNull(n1qlQuery, "n1qlQuery is required");
        return manager.get().n1qlQuery(n1qlQuery)
                .map(converter::toEntity)
                .map(d -> (T) d);
    }

    @Override
    public <T> Stream<T> search(SearchQuery query) {
        requireNonNull(query, "query is required");
        return manager.get().search(query)
                .map(converter::toEntity)
                .map(d -> (T) d);
    }

    @Override
    public <T> Stream<T> n1qlQuery(Statement n1qlQuery) {
        requireNonNull(n1qlQuery, "n1qlQuery is required");
        return manager.get().n1qlQuery(n1qlQuery)
                .map(converter::toEntity)
                .map(d -> (T) d);
    }
}
