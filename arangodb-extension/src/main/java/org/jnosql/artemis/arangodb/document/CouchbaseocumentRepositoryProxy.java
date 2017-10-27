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


import com.couchbase.client.java.document.json.JsonObject;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.query.AbstractDocumentRepository;
import org.jnosql.artemis.document.query.AbstractDocumentRepositoryProxy;
import org.jnosql.artemis.document.query.DocumentQueryDeleteParser;
import org.jnosql.artemis.document.query.DocumentQueryParser;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class CouchbaseocumentRepositoryProxy<T> extends AbstractDocumentRepositoryProxy<T> {

    private final Class<T> typeClass;

    private final ArangoDBTemplate template;


    private final DocumentCrudRepository repository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteParser;


    CouchbaseocumentRepositoryProxy(ArangoDBTemplate template, ClassRepresentations classRepresentations,
                                    Class<?> repositoryType, Reflections reflections) {
        this.template = template;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.repository = new DocumentCrudRepository(template, classRepresentation, reflections);
        this.queryParser = new DocumentQueryParser();
        this.deleteParser = new DocumentQueryDeleteParser();
    }


    @Override
    protected Repository getRepository() {
        return repository;
    }

    @Override
    protected DocumentQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected DocumentTemplate getTemplate() {
        return template;
    }

    @Override
    protected DocumentQueryDeleteParser getDeleteParser() {
        return deleteParser;
    }

    @Override
    protected ClassRepresentation getClassRepresentation() {
        return classRepresentation;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        AQL AQL = method.getAnnotation(AQL.class);
        if (Objects.nonNull(AQL)) {
            List<T> result = Collections.emptyList();
            JsonObject params = JsonObjectUtil.getParams(args, method);
            if (params.isEmpty()) {
                result = template.n1qlQuery(AQL.value());
            } else {
                result = template.n1qlQuery(AQL.value(), params);
            }
            return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
        }
        return super.invoke(o, method, args);
    }



    class DocumentCrudRepository extends AbstractDocumentRepository implements Repository {

        private final DocumentTemplate template;
        private final ClassRepresentation classRepresentation;
        private final Reflections reflections;

        DocumentCrudRepository(DocumentTemplate template, ClassRepresentation classRepresentation, Reflections reflections) {
            this.template = template;
            this.classRepresentation = classRepresentation;
            this.reflections = reflections;
        }


        @Override
        protected DocumentTemplate getTemplate() {
            return template;
        }

        @Override
        protected ClassRepresentation getClassRepresentation() {
            return classRepresentation;
        }

        @Override
        protected Reflections getReflections() {
            return reflections;
        }
    }
}
