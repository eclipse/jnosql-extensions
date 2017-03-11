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


import org.jnosql.artemis.CrudRepository;
import org.jnosql.artemis.document.DocumentRepository;
import org.jnosql.artemis.document.query.AbstractDocumentCrudRepository;
import org.jnosql.artemis.document.query.DocumentQueryDeleteParser;
import org.jnosql.artemis.document.query.DocumentQueryParser;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class OrientDBDocumentCrudRepositoryProxy<T> implements InvocationHandler {

    private final Class<T> typeClass;

    private final OrientDBDocumentRepository repository;


    private final DocumentCrudRepository crudRepository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteQueryParser;


    OrientDBDocumentCrudRepositoryProxy(OrientDBDocumentRepository repository, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.repository = repository;
        this.crudRepository = new DocumentCrudRepository(repository);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.deleteQueryParser = new DocumentQueryDeleteParser();
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        SQL sql = method.getAnnotation(SQL.class);
        if (Objects.nonNull(sql)) {
            List<T> result = Collections.emptyList();
            if (args == null || args.length == 0) {
                result = repository.find(sql.value());
            } else {
                result = repository.find(sql.value(), args);
            }
            return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
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
            return ReturnTypeConverterUtil.returnObject(query, repository, typeClass, method);
        } else if (methodName.startsWith("deleteBy")) {
            DocumentDeleteQuery query = deleteQueryParser.parse(methodName, args, classRepresentation);
            repository.delete(query);
            return null;
        }
        return null;
    }


    class DocumentCrudRepository extends AbstractDocumentCrudRepository implements CrudRepository {

        private final DocumentRepository repository;

        DocumentCrudRepository(DocumentRepository repository) {
            this.repository = repository;
        }

        @Override
        protected DocumentRepository getDocumentRepository() {
            return repository;
        }
    }
}
