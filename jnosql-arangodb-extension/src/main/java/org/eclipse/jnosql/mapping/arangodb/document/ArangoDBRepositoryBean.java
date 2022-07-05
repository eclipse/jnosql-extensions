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
package org.eclipse.jnosql.mapping.arangodb.document;

import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.document.DocumentRepositoryProducer;
import org.eclipse.jnosql.mapping.spi.AbstractBean;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;


class ArangoDBRepositoryBean extends AbstractBean<ArangoDBRepository> {

    private final Class type;


    private final Set<Type> types;

    private final Set<Annotation> qualifiers = Collections.singleton(new AnnotationLiteral<Default>() {
    });

    ArangoDBRepositoryBean(Class type, BeanManager beanManager) {
        super(beanManager);
        this.type = type;
        this.types = Collections.singleton(type);
    }

    @Override
    public Class<?> getBeanClass() {
        return type;
    }

    @Override
    public ArangoDBRepository create(CreationalContext<ArangoDBRepository> creationalContext) {
        ArangoDBTemplate template = getInstance(ArangoDBTemplate.class);
        DocumentRepositoryProducer producer = getInstance(DocumentRepositoryProducer.class);

        Repository<Object, Object> repository = producer.get((Class<Repository<Object, Object>>) type, template);

        ArangoDBDocumentRepositoryProxy handler = new ArangoDBDocumentRepositoryProxy(template,type, repository);
        return (ArangoDBRepository) Proxy.newProxyInstance(type.getClassLoader(),
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
        return type.getName() + '@' + "orientdb";
    }

}