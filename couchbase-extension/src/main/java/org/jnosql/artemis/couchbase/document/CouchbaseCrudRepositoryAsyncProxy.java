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
package org.jnosql.artemis.couchbase.document;


import com.couchbase.client.java.document.json.JsonObject;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.document.query.AbstractDocumentRepositoryAsync;
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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

class CouchbaseCrudRepositoryAsyncProxy<T> implements InvocationHandler {

    private static final Consumer NOOP = t -> {
    };

    private final Class<T> typeClass;

    private final CouchbaseTemplateAsync template;


    private final DocumentCrudRepositoryAsync crudRepository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser queryDeleteParser;


    CouchbaseCrudRepositoryAsyncProxy(CouchbaseTemplateAsync template, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.template = template;
        this.crudRepository = new DocumentCrudRepositoryAsync(template);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.queryDeleteParser = new DocumentQueryDeleteParser();
    }


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        N1QL n1QL = method.getAnnotation(N1QL.class);
        if (Objects.nonNull(n1QL)) {
            Consumer callBack = NOOP;
            if (Consumer.class.isInstance(args[args.length - 1])) {
                callBack = Consumer.class.cast(args[args.length - 1]);
            }

            Optional<JsonObject> params = getParams(args);
            if (params.isPresent()) {
                template.n1qlQuery(n1QL.value(), params.get(), callBack);
                return Void.class;
            } else {
                template.n1qlQuery(n1QL.value(), callBack);
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
                template.find(query, Consumer.class.cast(callBack));
            } else {
                throw new DynamicQueryException("On find async method you must put a java.util.function.Consumer" +
                        " as end parameter as callback");
            }
        } else if (methodName.startsWith("deleteBy")) {
            Object callBack = args[args.length - 1];
            DocumentDeleteQuery query = queryDeleteParser.parse(methodName, args, classRepresentation);
            if (Consumer.class.isInstance(callBack)) {
                template.delete(query, Consumer.class.cast(callBack));
            } else {
                template.delete(query);
            }
            return null;
        }
        return null;
    }

    private Optional<JsonObject> getParams(Object[] args) {
        return Stream.of(Optional.ofNullable(args).orElse(new Object[0]))
                .filter(a -> JsonObject.class.isInstance(a))
                .map(c -> JsonObject.class.cast(c))
                .findFirst();
    }

    class DocumentCrudRepositoryAsync extends AbstractDocumentRepositoryAsync implements RepositoryAsync {

        private final DocumentTemplateAsync template;

        DocumentCrudRepositoryAsync(DocumentTemplateAsync template) {
            this.template = template;
        }

        @Override
        protected DocumentTemplateAsync getTemplate() {
            return template;
        }
    }
}