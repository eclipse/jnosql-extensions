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
 *   Alessandro Moscatelli
 */
package org.eclipse.jnosql.mapping.criteria;

import org.eclipse.jnosql.communication.document.DocumentEntity;
import org.eclipse.jnosql.communication.document.DocumentManager;
import org.eclipse.jnosql.communication.document.DocumentQuery;
import org.eclipse.jnosql.mapping.Converters;
import org.eclipse.jnosql.mapping.document.DocumentEntityConverter;
import org.eclipse.jnosql.mapping.document.DocumentEventPersistManager;
import org.eclipse.jnosql.mapping.document.DocumentWorkflow;
import static java.util.Objects.requireNonNull;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jnosql.mapping.criteria.api.CriteriaQueryResult;
import org.eclipse.jnosql.mapping.criteria.api.EntityQuery;
import org.eclipse.jnosql.mapping.criteria.api.ExecutableQuery;
import org.eclipse.jnosql.mapping.criteria.api.ExpressionQuery;
import org.eclipse.jnosql.mapping.criteria.api.SelectQuery;
import org.eclipse.jnosql.mapping.criteria.api.CriteriaDocumentTemplate;
import org.eclipse.jnosql.mapping.document.AbstractDocumentTemplate;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;

/**
 * This class provides a delegating implementation of the
 * {@link CriteriaDocumentTemplate} interface, to augment the wrapped
 * DocumentTemplate with criteria API capabilities.
 *
 */
public class DefaultCriteriaDocumentTemplate extends AbstractDocumentTemplate implements CriteriaDocumentTemplate {

    private DocumentManager manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow workflow;

    private EntitiesMetadata entities;

    private Converters converters;

    private DocumentEventPersistManager persistManager;

    public DefaultCriteriaDocumentTemplate(
            DocumentManager manager,
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
    public <T, R extends CriteriaQueryResult<T>, Q extends ExecutableQuery<T, R, Q, F>, F> R executeQuery(ExecutableQuery<T, R, Q, F> criteriaQuery) {
        requireNonNull(criteriaQuery, "query is required");
        if (criteriaQuery instanceof SelectQuery) {
            SelectQuery<T, ?, ?, ?> selectQuery = SelectQuery.class.cast(criteriaQuery);
            DocumentQuery documentQuery = CriteriaQueryUtils.convert(selectQuery);
            this.getEventManager().firePreQuery(documentQuery);
            Stream<DocumentEntity> entityStream = this.getManager().select(
                    documentQuery
            );

            if (selectQuery instanceof EntityQuery) {
                EntityQuery.class.cast(selectQuery).feed(
                        entityStream.map(
                                documentEntity -> this.getConverter().toEntity(
                                        documentEntity
                                )
                        )
                );
            } else if (selectQuery instanceof ExpressionQuery) {
                ExpressionQuery.class.cast(selectQuery).feed(
                        entityStream.map(
                                documentEntity -> documentEntity.documents().stream().map(
                                        document -> document.value()
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

    @Override
    protected DocumentEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected DocumentManager getManager() {
        return manager;
    }

    @Override
    protected DocumentWorkflow getWorkflow() {
        return workflow;
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

}
