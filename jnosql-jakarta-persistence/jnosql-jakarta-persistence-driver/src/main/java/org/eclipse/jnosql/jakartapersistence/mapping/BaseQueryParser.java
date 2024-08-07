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

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.eclipse.jnosql.jakartapersistence.communication.PersistenceDatabaseManager;

class BaseQueryParser {

    protected final PersistenceDatabaseManager manager;

    protected BaseQueryParser(PersistenceDatabaseManager manager) {
        this.manager = manager;
    }

    protected <T> EntityType<T> findEntityType(String entityName) {
        return manager.findEntityType(entityName);
    }

    protected EntityManager entityManager() {
        return manager.getEntityManager();
    }

}
