/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.orientdb.document;


import org.jnosql.artemis.CrudRepositoryAsync;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.document.DocumentRepositoryAsync;
import org.jnosql.artemis.document.query.AbstractDocumentCrudRepositoryAsync;
import org.jnosql.artemis.document.query.DocumentQueryDeleteParser;
import org.jnosql.artemis.document.query.DocumentQueryParser;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

class OrientDBCrudRepositoryAsyncProxy<T> implements InvocationHandler {

    private static final Consumer NOOP = t -> {
    };

    private static final Predicate<Object> IS_NOT_CONSUMER = c -> !Consumer.class.isInstance(c);

    private final Class<T> typeClass;

    private final OrientDBDocumentRepositoryAsync repository;


    private final DocumentCrudRepositoryAsync crudRepository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser queryDeleteParser;


    OrientDBCrudRepositoryAsyncProxy(OrientDBDocumentRepositoryAsync repository, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.repository = repository;
        this.crudRepository = new DocumentCrudRepositoryAsync(repository);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.queryDeleteParser = new DocumentQueryDeleteParser();
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
                repository.find(sql.value(), callBack);
                return Void.class;
            } else {
                repository.find(sql.value(), callBack, Stream.of(args)
                        .filter(IS_NOT_CONSUMER)
                        .toArray(Object[]::new));
                return Void.class;
            }
        }
        String methodName = method.getName();
        switch (methodName) {
            case "save":
            case "update":
                return method.invoke(crudRepository, args);
            default:

        }
        if (methodName.startsWith("findBy")) {
            DocumentQuery query = queryParser.parse(methodName, args, classRepresentation);
            Object callBack = args[args.length - 1];
            if (Consumer.class.isInstance(callBack)) {
                repository.find(query, Consumer.class.cast(callBack));
            } else {
                throw new DynamicQueryException("On find async method you must put a java.util.function.Consumer" +
                        " as end parameter as callback");
            }
        } else if (methodName.startsWith("deleteBy")) {
            Object callBack = args[args.length - 1];
            DocumentDeleteQuery query = queryDeleteParser.parse(methodName, args, classRepresentation);
            if (Consumer.class.isInstance(callBack)) {
                repository.delete(query, Consumer.class.cast(callBack));
            } else {
                repository.delete(query);
            }
            return null;
        }
        return null;
    }


    class DocumentCrudRepositoryAsync extends AbstractDocumentCrudRepositoryAsync implements CrudRepositoryAsync {

        private final DocumentRepositoryAsync repository;

        DocumentCrudRepositoryAsync(DocumentRepositoryAsync repository) {
            this.repository = repository;
        }

        @Override
        protected DocumentRepositoryAsync getDocumentRepository() {
            return repository;
        }
    }
}