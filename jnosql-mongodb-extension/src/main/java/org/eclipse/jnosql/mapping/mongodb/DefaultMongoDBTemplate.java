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

import org.eclipse.jnosql.mapping.mongodb.criteria.api.CriteriaQuery;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.CriteriaQueryResult;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.EntityQuery;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.ExecutableQuery;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.ExpressionQuery;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.SelectQuery;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.document.DocumentQuery;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.eclipse.jnosql.communication.mongodb.document.MongoDBDocumentCollectionManager;
import org.eclipse.jnosql.mapping.document.AbstractDocumentTemplate;
import org.eclipse.jnosql.mapping.reflection.ClassMapping;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jnosql.mapping.mongodb.criteria.CriteriaQueryUtils;
import org.eclipse.jnosql.mapping.mongodb.criteria.DefaultCriteriaQuery;

@Typed(MongoDBTemplate.class)
class DefaultMongoDBTemplate extends AbstractDocumentTemplate implements MongoDBTemplate {

    private Instance<MongoDBDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow workflow;

    private EntitiesMetadata entities;

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
            EntitiesMetadata entities,
            Converters converters,
            DocumentEventPersistManager persistManager) {
        this.manager = manager;
        this.converter = converter;
        this.workflow = workflow;
        this.entities = entities;
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
    protected EntitiesMetadata getEntities() {
        return entities;
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
        Objects.requireNonNull(collectionName, "collectionName is required");
        Objects.requireNonNull(filter, "filter is required");
        Stream<DocumentEntity> entityStream = this.getManager().select(collectionName, filter);
        return entityStream.map(this.converter::toEntity);
    }

    @Override
    public <T> Stream<T> select(Class<T> entity, Bson filter) {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(filter, "filter is required");
        ClassMapping mapping = this.mappings.get(entity);
        Stream<DocumentEntity> entityStream = this.getManager().select(mapping.getName(), filter);
        return entityStream.map(this.converter::toEntity);
    }

    @Override
    public Stream<Map<String, BsonValue>> aggregate(String collectionName, List<Bson> pipeline) {
        Objects.requireNonNull(collectionName, "collectionName is required");
        Objects.requireNonNull(pipeline, "pipeline is required");
        return this.getManager().aggregate(collectionName, pipeline);
    }

    @Override
    public <T> Stream<Map<String, BsonValue>> aggregate(Class<T> entity, List<Bson> pipeline) {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(pipeline, "pipeline is required");
        ClassMapping mapping = this.mappings.get(entity);
        return this.getManager().aggregate(mapping.getName(), pipeline);
    }

    @Override
    public <T> CriteriaQuery<T> createQuery(Class<T> type) {
        return new DefaultCriteriaQuery<>(type);
    }

    @Override
    public <T, R extends CriteriaQueryResult<T>, Q extends ExecutableQuery<T, R, Q, F>, F> R executeQuery(ExecutableQuery<T, R, Q, F> criteriaQuery) {
        requireNonNull(criteriaQuery, "query is required");
        if (criteriaQuery instanceof SelectQuery) {
            SelectQuery<T, ?, ?, ?> selectQuery = SelectQuery.class.cast(criteriaQuery);
            DocumentQuery documentQuery = CriteriaQueryUtils.convert(selectQuery);
            getPersistManager().firePreQuery(documentQuery);
            Stream<DocumentEntity> entityStream = getManager().select(
                    documentQuery
            );

            if (selectQuery instanceof EntityQuery) {
                EntityQuery.class.cast(selectQuery).feed(
                        entityStream.map(
                                documentEntity -> getConverter().toEntity(
                                        documentEntity
                                )
                        )
                );
            } else if (selectQuery instanceof ExpressionQuery) {
                ExpressionQuery.class.cast(selectQuery).feed(
                        entityStream.map(
                                documentEntity -> documentEntity.getDocuments().stream().map(
                                        document -> document.getValue()
                                ).collect(
                                        Collectors.toList()
                                )
                        )
                );
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        return criteriaQuery.getResult();
    }

}
