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
package org.eclipse.jnosql.jakartapersistence.mapping.spi;

import org.eclipse.jnosql.jakartapersistence.communication.PersistenceClassScanner;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import org.eclipse.jnosql.mapping.document.query.RepositoryDocumentBean;
import org.eclipse.jnosql.mapping.metadata.ClassScanner;

import java.util.Set;
import java.util.logging.Logger;

/**
 * This CDI extension, {@code JakartaPersistenceExtension}, observes the CDI container lifecycle events to perform tasks
 * related to Jakarta Persistence repository beans.
 * <p>
 */
public class JakartaPersistenceExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(JakartaPersistenceExtension.class.getName());

    void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery) {

        ClassScanner scanner = new PersistenceClassScanner();

        Set<Class<?>> crudTypes = scanner.repositoriesStandard();

        LOGGER.info(() -> "Processing Jakarta Persistence extension: crud "
                + crudTypes.size() + " found");
        LOGGER.fine(() -> "Processing repositories as a Jakarta Persistence implementation: " + crudTypes);

        crudTypes.forEach(type -> {
            afterBeanDiscovery.addBean(new RepositoryDocumentBean<>(type, ""));
        });

    }
}