/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.orientdb.document;

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

class OrientDBExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(OrientDBExtension.class.getName());

    private final Collection<Class<?>> crudTypes = new HashSet<>();

    private final Collection<Class<?>> crudAsyncTypes = new HashSet<>();


    <T extends OrientDBCrudRepository> void onProcessAnnotatedType(@Observes final ProcessAnnotatedType<T> repo) {
        Class<T> javaClass = repo.getAnnotatedType().getJavaClass();

        if(OrientDBCrudRepository.class.equals(javaClass)) {
            return;
        }

        if (Stream.of(javaClass.getInterfaces()).anyMatch(c -> OrientDBCrudRepository.class.equals(c))
                && Modifier.isInterface(javaClass.getModifiers())) {
            crudTypes.add(javaClass);
        }
    }

    <T extends OrientDBCrudRepositoryAsync> void onProcessAnnotatedTypeAsync(@Observes final ProcessAnnotatedType<T> repo) {
        Class<T> javaClass = repo.getAnnotatedType().getJavaClass();

        if(OrientDBCrudRepositoryAsync.class.equals(javaClass)) {
            return;
        }

        if (Stream.of(javaClass.getInterfaces()).anyMatch(c -> OrientDBCrudRepositoryAsync.class.equals(c))
                && Modifier.isInterface(javaClass.getModifiers())) {
            crudAsyncTypes.add(javaClass);
        }
    }


    void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        LOGGER.info("Starting the onAfterBeanDiscovery with elements number: " + crudTypes.size());

        crudTypes.forEach(type -> {
            afterBeanDiscovery.addBean(new OrientDBRepositoryBean(type, beanManager));
        });

        crudAsyncTypes.forEach(type -> {
            afterBeanDiscovery.addBean(new OrientDBRepositoryAsyncBean(type, beanManager));
        });

        LOGGER.info("Finished the onAfterBeanDiscovery");
    }
}
