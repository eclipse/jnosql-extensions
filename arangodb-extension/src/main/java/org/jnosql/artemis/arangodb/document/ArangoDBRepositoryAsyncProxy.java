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


import jakarta.nosql.mapping.RepositoryAsync;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.Collections.emptyMap;

class ArangoDBRepositoryAsyncProxy<T> implements InvocationHandler {

    private static final Consumer NOOP = t -> {
    };


    private final ArangoDBTemplateAsync template;

    private final RepositoryAsync<?, ?> repositoryAsync;


    ArangoDBRepositoryAsyncProxy(ArangoDBTemplateAsync template, RepositoryAsync<?, ?> repositoryAsync) {
        this.template = template;
        this.repositoryAsync = repositoryAsync;
    }


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        AQL aql = method.getAnnotation(AQL.class);
        if (Objects.nonNull(aql)) {
            Consumer callBack = NOOP;
            if (Consumer.class.isInstance(args[args.length - 1])) {
                callBack = Consumer.class.cast(args[args.length - 1]);
            }

            Map<String, Object> params = ParamUtil.getParams(args, method);
            if (params.isEmpty()) {
                template.aql(aql.value(), emptyMap(), callBack);
                return Void.class;
            } else {
                template.aql(aql.value(), params, callBack);
                return Void.class;
            }
        }
        return method.invoke(repositoryAsync, args);
    }


}