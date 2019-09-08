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
package org.eclipse.jnosql.artemis.orientdb.document;


import jakarta.nosql.mapping.RepositoryAsync;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

class OrientDBRepositoryAsyncProxy<T> implements InvocationHandler {

    private static final Consumer NOOP = t -> {
    };

    private static final Predicate<Object> IS_NOT_CONSUMER = c -> !Consumer.class.isInstance(c);


    private final OrientDBTemplateAsync template;

    private final RepositoryAsync<?, ?> repositoryAsync;

    OrientDBRepositoryAsyncProxy(OrientDBTemplateAsync template, RepositoryAsync<?, ?> repositoryAsync) {
        this.template = template;
        this.repositoryAsync = repositoryAsync;
    }


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        SQL sql = method.getAnnotation(SQL.class);
        if (Objects.nonNull(sql)) {
            Consumer callBack = NOOP;
            if (Consumer.class.isInstance(args[args.length - 1])) {
                callBack = Consumer.class.cast(args[args.length - 1]);
            }

            if (args == null || args.length == 1) {
                template.sql(sql.value(), callBack);
                return Void.class;
            } else {
                Map<String, Object> params = MapTypeUtil.getParams(args, method);
                if (params.isEmpty()) {
                    template.sql(sql.value(), callBack, Stream.of(args)
                            .filter(IS_NOT_CONSUMER)
                            .toArray(Object[]::new));
                } else {
                    template.sql(sql.value(), callBack, params);
                }
                return Void.class;
            }
        }
        return method.invoke(repositoryAsync, args);
    }

}