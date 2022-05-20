/*
 *  Copyright (c) 2022 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.mongodb;

import jakarta.nosql.document.DocumentCollectionManager;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.eclipse.jnosql.communication.mongodb.document.MongoDBDocumentCollectionManager;
import org.eclipse.jnosql.mapping.document.AbstractDocumentTemplate;
import org.eclipse.jnosql.mapping.reflection.ClassMapping;
import org.eclipse.jnosql.mapping.reflection.ClassMappings;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Typed(MongoDBTemplate.class)
class DefaultMongoDBTemplate extends AbstractDocumentTemplate implements MongoDBTemplate {

    private Instance<MongoDBDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow workflow;

    private ClassMappings mappings;

    private Converters converters;

    private DocumentEventPersistManager persistManager;

    /**
     * To CDI only
     */
    @Deprecated
    DefaultMongoDBTemplate() {
    }

    DefaultMongoDBTemplate(Instance<MongoDBDocumentCollectionManager> manager,
                           DocumentEntityConverter converter,
                           DocumentWorkflow workflow,
                           ClassMappings mappings,
                           Converters converters,
                           DocumentEventPersistManager persistManager) {
        this.manager = manager;
        this.converter = converter;
        this.workflow = workflow;
        this.mappings = mappings;
        this.converters = converters;
        this.persistManager = persistManager;
    }

    @Override
    protected DocumentEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected MongoDBDocumentCollectionManager getManager() {
        return manager.get();
    }

    @Override
    protected DocumentWorkflow getWorkflow() {
        return workflow;
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
    public long delete(String collectionName, Bson filter) {
        Objects.requireNonNull(collectionName, "collectionName is required");
        Objects.requireNonNull(filter, "filter is required");
        return this.getManager().delete(collectionName, filter);
    }

    @Override
    public <T> long delete(Class<T> entity, Bson filter) {
        Objects.requireNonNull(entity, "Entity is required");
        Objects.requireNonNull(filter, "filter is required");
        ClassMapping mapping = this.mappings.get(entity);
        return this.getManager().delete(mapping.getName(), filter);
    }

    @Override
    public <T> Stream<T> select(String collectionName, Bson filter) {
        return null;
    }

    @Override
    public <T> Stream<T> select(Class<T> entity, Bson filter) {
        return null;
    }

    @Override
    public Stream<Map<String, BsonValue>> aggregate(String collectionName, List<Bson> pipeline) {
        return null;
    }
}
