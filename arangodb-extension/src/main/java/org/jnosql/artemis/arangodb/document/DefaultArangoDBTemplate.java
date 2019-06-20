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


import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import jakarta.nosql.mapping.reflection.ClassMappings;
import jakarta.nosql.document.DocumentCollectionManager;
import org.jnosql.artemis.document.AbstractDocumentTemplate;
import org.jnosql.diana.arangodb.document.ArangoDBDocumentCollectionManager;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * The Default implementation of {@link ArangoDBTemplate}
 */
@Typed(ArangoDBTemplate.class)
class DefaultArangoDBTemplate extends AbstractDocumentTemplate implements ArangoDBTemplate {

    private Instance<ArangoDBDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    private ClassMappings mappings;

    private Converters converters;

    @Inject
    DefaultArangoDBTemplate(Instance<ArangoDBDocumentCollectionManager> manager,
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

    DefaultArangoDBTemplate() {
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
    public <T> List<T> aql(String query, Map<String, Object> values) {
        requireNonNull(query, "query is required");
        requireNonNull(values, "values is required");
        return manager.get().aql(query, values).stream().map(converter::toEntity).map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> aql(String query, Map<String, Object> values, Class<T> typeClass) {
        return manager.get().aql(query, values, typeClass);
    }

    @Override
    public <T> List<T> aql(String query, Class<T> typeClass) {
        return manager.get().aql(query, typeClass);
    }
}
