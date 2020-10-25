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
package org.eclipse.jnosql.mapping.hazelcast.keyvalue;

import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.keyvalue.KeyValueRepositoryProducer;
import org.eclipse.jnosql.artemis.spi.AbstractBean;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;


class HazelcastRepositoryBean extends AbstractBean<HazelcastRepository> {

    private final Class type;

    private final Set<Type> types;

    private final Set<Annotation> qualifiers = Collections.singleton(new AnnotationLiteral<Default>() {
    });

    HazelcastRepositoryBean(Class type, BeanManager beanManager) {
        super(beanManager);
        this.type = type;
        this.types = Collections.singleton(type);
    }

    @Override
    public Class<?> getBeanClass() {
        return type;
    }


    @Override
    public HazelcastRepository create(CreationalContext<HazelcastRepository> creationalContext) {
        HazelcastTemplate template = getInstance(HazelcastTemplate.class);

        KeyValueRepositoryProducer producer = getInstance(KeyValueRepositoryProducer.class);
        Repository<?, ?> repository = producer.get((Class<Repository<Object, Object>>) type, template);
        HazelcastRepositoryProxy handler = new HazelcastRepositoryProxy(template, type, repository);
        return (HazelcastRepository) Proxy.newProxyInstance(type.getClassLoader(),
                new Class[]{type},
                handler);
    }


    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public String getId() {
        return type.getName() + '@' + "hazelcast";
    }

}