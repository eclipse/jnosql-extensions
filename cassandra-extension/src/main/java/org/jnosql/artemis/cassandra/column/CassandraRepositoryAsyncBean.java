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
package org.jnosql.artemis.cassandra.column;

import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.column.ColumnRepositoryAsyncProducer;
import org.jnosql.artemis.spi.AbstractBean;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;


class CassandraRepositoryAsyncBean extends AbstractBean<CassandraRepositoryAsync>{

    private final Class type;


    private final Set<Type> types;

    private final Set<Annotation> qualifiers = Collections.singleton(new AnnotationLiteral<Default>() {
    });

    CassandraRepositoryAsyncBean(Class type, BeanManager beanManager) {
        super(beanManager);
        this.type = type;
        this.types = Collections.singleton(type);
    }

    @Override
    public Class<?> getBeanClass() {
        return type;
    }

    @Override
    public CassandraRepositoryAsync create(CreationalContext<CassandraRepositoryAsync> creationalContext) {
        CassandraTemplateAsync templateAsync = getInstance(CassandraTemplateAsync.class);
        ColumnRepositoryAsyncProducer producer = getInstance(ColumnRepositoryAsyncProducer.class);
        RepositoryAsync repositoryAsync = producer.get((Class<RepositoryAsync<Object, Object>>) type, templateAsync);
        CassandraRepositoryAsyncProxy handler = new CassandraRepositoryAsyncProxy(templateAsync, repositoryAsync);
        return (CassandraRepositoryAsync) Proxy.newProxyInstance(type.getClassLoader(),
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
        return type.getName() + "Async@cassandra";
    }

}