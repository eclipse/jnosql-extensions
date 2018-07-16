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


import org.jnosql.artemis.Repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.jnosql.artemis.cassandra.column.CQLObjectUtil.getValues;

class CassandraRepositoryProxy<T> implements InvocationHandler {


    private final Class<T> typeClass;

    private final CassandraTemplate template;

    private final Repository<T,?> repository;


    CassandraRepositoryProxy(CassandraTemplate template, Class<?> repositoryType, Repository<T, ?> repository) {

        this.template = template;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.repository = repository;
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        CQL cql = method.getAnnotation(CQL.class);
        if (Objects.nonNull(cql)) {

            List<T> result;
            Map<String, Object> values = getValues(args, method);
            if (!values.isEmpty()) {
                result = template.cql(cql.value(), values);
            } else if (args == null || args.length == 0) {
                result = template.cql(cql.value());
            } else {
                result = template.cql(cql.value(), args);
            }
            return CassandraReturnTypeConverterUtil.returnObject(result, typeClass, method);
        }

        return method.invoke(repository, args);
    }

}
