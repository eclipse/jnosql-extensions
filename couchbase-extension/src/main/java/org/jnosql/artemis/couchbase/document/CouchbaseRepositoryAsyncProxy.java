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
package org.jnosql.artemis.couchbase.document;


import com.couchbase.client.java.document.json.JsonObject;
import org.jnosql.artemis.RepositoryAsync;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;

import static org.jnosql.artemis.couchbase.document.JsonObjectUtil.getParams;

class CouchbaseRepositoryAsyncProxy<T>  implements InvocationHandler {

    private static final Consumer NOOP = t -> {
    };


    private final CouchbaseTemplateAsync template;


    private final RepositoryAsync<?, ?> repositoryAsync;


    CouchbaseRepositoryAsyncProxy(CouchbaseTemplateAsync template, RepositoryAsync<?, ?> repositoryAsync) {
        this.template = template;
        this.repositoryAsync = repositoryAsync;
    }



    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        N1QL n1QL = method.getAnnotation(N1QL.class);
        if (Objects.nonNull(n1QL)) {
            Consumer callBack = NOOP;
            if (Consumer.class.isInstance(args[args.length - 1])) {
                callBack = Consumer.class.cast(args[args.length - 1]);
            }

            JsonObject params = getParams(args, method);
            if (params.isEmpty()) {
                template.n1qlQuery(n1QL.value(), callBack);
                return Void.class;
            } else {
                template.n1qlQuery(n1QL.value(), params, callBack);
                return Void.class;
            }
        }
        return method.invoke(repositoryAsync, args);
    }

}