/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.orientdb.document;


import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.document.query.AbstractDocumentRepositoryAsync;
import org.jnosql.artemis.document.query.AbstractDocumentRepositoryAsyncProxy;
import org.jnosql.artemis.document.query.DocumentQueryDeleteParser;
import org.jnosql.artemis.document.query.DocumentQueryParser;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

class OrientDBRepositoryAsyncProxy<T> extends AbstractDocumentRepositoryAsyncProxy<T> {

    private static final Consumer NOOP = t -> {
    };

    private static final Predicate<Object> IS_NOT_CONSUMER = c -> !Consumer.class.isInstance(c);

    private final Class<T> typeClass;

    private final OrientDBDocumentTemplateAsync template;


    private final DocumentRepositoryAsync repository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteParser;


    OrientDBRepositoryAsyncProxy(OrientDBDocumentTemplateAsync template, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.template = template;
        this.repository = new DocumentRepositoryAsync(template);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.deleteParser = new DocumentQueryDeleteParser();
    }


    @Override
    protected RepositoryAsync getRepository() {
        return repository;
    }

    @Override
    protected DocumentQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected DocumentTemplateAsync getTemplate() {
        return null;
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
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        SQL sql = method.getAnnotation(SQL.class);
        if (Objects.nonNull(sql)) {
            Consumer callBack = NOOP;
            if (Consumer.class.isInstance(args[args.length - 1])) {
                callBack = Consumer.class.cast(args[args.length - 1]);
            }

            if (args == null || args.length == 1) {
                template.find(sql.value(), callBack);
                return Void.class;
            } else {
                template.find(sql.value(), callBack, Stream.of(args)
                        .filter(IS_NOT_CONSUMER)
                        .toArray(Object[]::new));
                return Void.class;
            }
        }
        return super.invoke(instance, method, args);
    }


    class DocumentRepositoryAsync extends AbstractDocumentRepositoryAsync implements RepositoryAsync {

        private final DocumentTemplateAsync template;

        DocumentRepositoryAsync(DocumentTemplateAsync template) {
            this.template = template;
        }

        @Override
        protected DocumentTemplateAsync getTemplate() {
            return template;
        }
    }
}