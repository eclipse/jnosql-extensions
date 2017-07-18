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
package org.jnosql.artemis.graph;

import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;

import javax.inject.Inject;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation {@link VertexConverter}
 * This Default implementation uses {@link DocumentEntityConverter} and then converts to Graph
 */
class DefaultVertexConverter implements VertexConverter {

    @Inject
    private DocumentEntityConverter converter;

    @Override
    public ArtemisVertex toGraph(Object entityInstance) throws NullPointerException {

        DocumentEntity entity = converter.toDocument(entityInstance);
        ArtemisVertex vertex = ArtemisVertex.of(entity.getName());

        entity.getDocuments().forEach(d -> this.toProperty(vertex, d));
        return vertex;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, ArtemisVertex entity) throws NullPointerException {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(entity, "entity is required");
        DocumentEntity documentEntity = toDocumentEntity(entity);
        return converter.toEntity(entityClass, documentEntity);
    }

    @Override
    public <T> T toEntity(ArtemisVertex entity) throws NullPointerException {
        requireNonNull(entity, "entity is required");
        return converter.toEntity(toDocumentEntity(entity));
    }

    private DocumentEntity toDocumentEntity(ArtemisVertex entity) {
        DocumentEntity documentEntity = DocumentEntity.of(entity.getLabel());
        entity.getKeys()
                .stream()
                .map(k -> Document.of(k, entity.get(k).get()))
                .forEach(documentEntity::add);
        return documentEntity;
    }

    private void toProperty(ArtemisVertex vertex, Document document) {
        Object value = document.get();
        if (Document.class.isInstance(value)) {
            Document subDocument = Document.class.cast(value);
            List<Document> documents = subDocument.get(new TypeReference<List<Document>>() {
            });
            documents.forEach(d -> this.toProperty(vertex, d));
        } else {
            vertex.add(document.getName(), document.getValue());
        }
    }

}
