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
package org.jnosql.artemis.arangodb.document;

import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.document.DocumentRepositoryAsyncProducer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;


class ArangoDBRepositoryAsyncBean implements Bean<ArangoDBRepositoryAsync>, PassivationCapable {

    private final Class type;

    private final BeanManager beanManager;

    private final Set<Type> types;

    private final Set<Annotation> qualifiers = Collections.singleton(new AnnotationLiteral<Default>() {
    });

    ArangoDBRepositoryAsyncBean(Class type, BeanManager beanManager) {
        this.type = type;
        this.beanManager = beanManager;
        this.types = Collections.singleton(type);
    }

    @Override
    public Class<?> getBeanClass() {
        return type;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public ArangoDBRepositoryAsync create(CreationalContext<ArangoDBRepositoryAsync> creationalContext) {
        ArangoDBTemplateAsync templateAsync = getInstance(ArangoDBTemplateAsync.class);
        DocumentRepositoryAsyncProducer producer = getInstance(DocumentRepositoryAsyncProducer.class);
        RepositoryAsync repositoryAsync = producer.get((Class<RepositoryAsync<Object, Object>>) type, templateAsync);
        ArangoDBRepositoryAsyncProxy handler = new ArangoDBRepositoryAsyncProxy(templateAsync, repositoryAsync);
        return (ArangoDBRepositoryAsync) Proxy.newProxyInstance(type.getClassLoader(),
                new Class[]{type},
                handler);
    }


    private <T> T getInstance(Class<T> clazz) {
        Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }

    @Override
    public void destroy(ArangoDBRepositoryAsync instance, CreationalContext<ArangoDBRepositoryAsync> creationalContext) {

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
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public String getId() {
        return type.getName() + "Async@arangodb";
    }

}