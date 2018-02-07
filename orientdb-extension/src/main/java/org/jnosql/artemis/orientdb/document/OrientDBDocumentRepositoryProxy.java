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
package org.jnosql.artemis.orientdb.document;


import org.jnosql.artemis.Converters;
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
import java.util.Map;
import java.util.Objects;

class OrientDBDocumentRepositoryProxy<T> extends AbstractDocumentRepositoryProxy<T> {

    private final Class<T> typeClass;

    private final OrientDBTemplate template;

    private final DocumentRepository repository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteParser;

    private final Converters converters;


    OrientDBDocumentRepositoryProxy(OrientDBTemplate template, ClassRepresentations classRepresentations,
                                    Class<?> repositoryType, Reflections reflections, Converters converters) {
        this.template = template;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.repository = new DocumentRepository(template, classRepresentation, reflections);
        this.queryParser = new DocumentQueryParser();
        this.deleteParser = new DocumentQueryDeleteParser();
        this.converters = converters;
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
    protected Converters getConverters() {
        return converters;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        SQL sql = method.getAnnotation(SQL.class);
        if (Objects.nonNull(sql)) {
            List<T> result;

            if (args == null || args.length == 0) {
                result = template.sql(sql.value());
            } else {
                Map<String, Object> params = MapTypeUtil.getParams(args, method);
                if (params.isEmpty()) {
                    result = template.sql(sql.value(), args);
                } else {
                    result = template.sql(sql.value(), params);
                }
            }
            return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
        }
        return super.invoke(o, method, args);
    }


    class DocumentRepository extends AbstractDocumentRepository implements Repository {

        private final DocumentTemplate template;
        private final ClassRepresentation classRepresentation;
        private final Reflections reflections;

        DocumentRepository(DocumentTemplate template, ClassRepresentation classRepresentation, Reflections reflections) {
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
