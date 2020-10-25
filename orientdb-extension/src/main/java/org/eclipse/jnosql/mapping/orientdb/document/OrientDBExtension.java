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
package org.eclipse.jnosql.mapping.orientdb.document;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class OrientDBExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(OrientDBExtension.class.getName());

    private final Collection<Class<?>> crudTypes = new HashSet<>();

    <T extends OrientDBCrudRepository> void onProcessAnnotatedType(@Observes final ProcessAnnotatedType<T> repo) {
        Class<T> javaClass = repo.getAnnotatedType().getJavaClass();

        if(OrientDBCrudRepository.class.equals(javaClass)) {
            return;
        }

        if (Stream.of(javaClass.getInterfaces()).anyMatch(OrientDBCrudRepository.class::equals)
                && Modifier.isInterface(javaClass.getModifiers())) {
            crudTypes.add(javaClass);
        }
    }


    void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        LOGGER.info("Starting the onAfterBeanDiscovery with elements number: " + crudTypes.size());

        crudTypes.forEach(type -> afterBeanDiscovery.addBean(new OrientDBRepositoryBean(type, beanManager)));

        LOGGER.info("Finished the onAfterBeanDiscovery");
    }
}
