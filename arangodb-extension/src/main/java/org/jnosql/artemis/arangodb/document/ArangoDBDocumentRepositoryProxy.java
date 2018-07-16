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


import org.jnosql.artemis.Repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyMap;

class ArangoDBDocumentRepositoryProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final ArangoDBTemplate template;

    private final Repository<?, ?> repository;


    ArangoDBDocumentRepositoryProxy(ArangoDBTemplate template, Class<?> repositoryType, Repository<?, ?> repository) {
        this.template = template;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.repository = repository;
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        AQL aql = method.getAnnotation(AQL.class);
        if (Objects.nonNull(aql)) {
            List<T> result;
            Map<String, Object> params = ParamUtil.getParams(args, method);
            if (params.isEmpty()) {
                result = template.aql(aql.value(), emptyMap());
            } else {
                result = template.aql(aql.value(), params);
            }
            return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
        }
        return method.invoke(repository, args);
    }


}
