/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.mapping.solr.document;


import jakarta.nosql.document.DocumentManager;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;
import org.eclipse.jnosql.mapping.document.AbstractDocumentTemplate;
import org.eclipse.jnosql.communication.solr.document.SolrDocumentManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * The Default implementation of {@link SolrTemplate}
 */
@Typed(SolrTemplate.class)
@ApplicationScoped
class DefaultSolrTemplate extends AbstractDocumentTemplate implements SolrTemplate {

    private Instance<SolrDocumentManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    private EntitiesMetadata entities;

    private Converters converters;

    @Inject
    DefaultSolrTemplate(Instance<SolrDocumentManager> manager,
                        DocumentEntityConverter converter, DocumentWorkflow flow,
                        DocumentEventPersistManager persistManager,
                        EntitiesMetadata entities,
                        Converters converters) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
        this.entities = entities;
        this.converters = converters;
    }

    DefaultSolrTemplate() {
    }

    @Override
    protected DocumentEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected DocumentManager getManager() {
        return manager.get();
    }

    @Override
    protected DocumentWorkflow getWorkflow() {
        return flow;
    }

    @Override
    protected DocumentEventPersistManager getEventManager() {
        return persistManager;
    }

    @Override
    protected EntitiesMetadata getEntities() {
        return entities;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @Override
    public <T> List<T> solr(String query) {
        requireNonNull(query, "query is required");
        return manager.get().solr(query).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> solr(String query, Map<String, ?> params) {
        requireNonNull(query, "query is required");
        requireNonNull(params, "params is required");
        return manager.get().solr(query, params).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }
}
