/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *  You may elect to redistribute this code under either of these licenses.
 *
 *  Contributors:
 *
 *  Ondro Mihalyi
 */
package org.eclipse.jnosql.jakartapersistence.mapping;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import org.eclipse.jnosql.jakartapersistence.communication.PersistenceDatabaseManager;

class DeleteQueryParser extends BaseQueryParser {


    public DeleteQueryParser(PersistenceDatabaseManager manager) {
        super(manager);
    }

    public <T, K> void delete(Class<T> type, K key) {
        CriteriaBuilder criteriaBuilder = entityManager().getCriteriaBuilder();
        CriteriaDelete<T> deleteCriteria = criteriaBuilder.createCriteriaDelete(type);
        Root<?> root = deleteCriteria.from(type);
        String entityIdName = getEntityIdName(type);
        deleteCriteria.where(criteriaBuilder.equal(root.get(entityIdName), key));
        entityManager().createQuery(deleteCriteria).executeUpdate();
    }

    private <T> String getEntityIdName(Class<T> type) {
        EntityType<T> entityType = entityManager().getMetamodel().entity(type);
        SingularAttribute<?, ?> idAttribute = entityType.getId(entityType.getIdType().getJavaType());
        String entityIdName = idAttribute.getName();
        return entityIdName;
    }


}
