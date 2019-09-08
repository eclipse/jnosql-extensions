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
package org.eclipse.jnosql.artemis.cassandra.column;

import jakarta.nosql.mapping.RepositoryAsync;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.eclipse.jnosql.artemis.cassandra.column.CQLObjectUtil.getValues;

class CassandraRepositoryAsyncProxy<T> implements InvocationHandler {


    private static final Predicate<Object> IS_NOT_CONSUMER = c -> !Consumer.class.isInstance(c);
    private static final Predicate<Object> IS_VALID_PARAMETER = IS_NOT_CONSUMER;
    private static final Consumer NOOP = t -> {
    };

    private final CassandraTemplateAsync templateAsync;

    private RepositoryAsync<?, ?> repositoryAsync;


    CassandraRepositoryAsyncProxy(CassandraTemplateAsync repository, RepositoryAsync<?, ?> repositoryAsync) {
        this.templateAsync = repository;
        this.repositoryAsync = repositoryAsync;
    }


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {


        CQL cql = method.getAnnotation(CQL.class);
        if (Objects.nonNull(cql)) {
            Consumer callBack = NOOP;
            if (Consumer.class.isInstance(args[args.length - 1])) {
                callBack = Consumer.class.cast(args[args.length - 1]);
            }
            Map<String, Object> values = getValues(args, method);
            if (!values.isEmpty()) {
                templateAsync.cql(cql.value(), values, callBack);
                return Void.class;
            } else if (args == null || args.length == 1) {
                templateAsync.cql(cql.value(), callBack);
                return Void.class;
            } else {
                templateAsync.cql(cql.value(), callBack, Stream.of(args)
                        .filter(IS_VALID_PARAMETER)
                        .toArray(Object[]::new));
                return Void.class;
            }
        }
        return method.invoke(repositoryAsync, args);
    }


}
