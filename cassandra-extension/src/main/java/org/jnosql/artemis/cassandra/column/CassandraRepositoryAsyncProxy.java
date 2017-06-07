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
package org.jnosql.artemis.cassandra.column;

import com.datastax.driver.core.ConsistencyLevel;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.column.ColumnTemplateAsync;
import org.jnosql.artemis.column.query.AbstractColumnRepositoryAsync;
import org.jnosql.artemis.column.query.ColumnQueryDeleteParser;
import org.jnosql.artemis.column.query.ColumnQueryParser;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

class CassandraRepositoryAsyncProxy<T> implements InvocationHandler {

    private static final String SAVE = "save";
    private static final String UPDATE = "update";
    private static final String FIND_BY = "findBy";
    private static final String DELETE_BY = "deleteBy";

    private static final Predicate<Object> IS_NOT_CONSISTENCY_LEVEL = c -> !ConsistencyLevel.class.isInstance(c);
    private static final Predicate<Object> IS_NOT_CONSUMER = c -> !Consumer.class.isInstance(c);
    private static final Predicate<Object> IS_VALID_PARAMETER = IS_NOT_CONSISTENCY_LEVEL.and(IS_NOT_CONSUMER);
    private static final Consumer NOOP = t -> {
    };

    private final Class<T> typeClass;

    private final CassandraTemplateAsync repository;

    private final ColumnRepositoryAsync crudRepository;

    private final ClassRepresentation classRepresentation;

    private final ColumnQueryParser queryParser;

    private final ColumnQueryDeleteParser queryDeleteParser;


    CassandraRepositoryAsyncProxy(CassandraTemplateAsync repository, ClassRepresentations classRepresentations,
                                  Class<?> repositoryType, Reflections reflections) {
        this.repository = repository;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.crudRepository = new ColumnRepositoryAsync(repository, classRepresentation, reflections);
        this.queryParser = new ColumnQueryParser();
        this.queryDeleteParser = new ColumnQueryDeleteParser();
    }


    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {


        CQL cql = method.getAnnotation(CQL.class);
        if (Objects.nonNull(cql)) {
            Consumer callBack = NOOP;
            if (Consumer.class.isInstance(args[args.length - 1])) {
                callBack = Consumer.class.cast(args[args.length - 1]);
            }

            if (args == null || args.length == 1) {
                repository.cql(cql.value(), callBack);
                return Void.class;
            } else {
                repository.cql(cql.value(), callBack, Stream.of(args)
                        .filter(IS_VALID_PARAMETER)
                        .toArray(Object[]::new));
                return Void.class;
            }
        }

        String methodName = method.getName();
        switch (methodName) {
            case SAVE:
            case UPDATE:
                return method.invoke(crudRepository, args);
            default:
        }


        if (methodName.startsWith(FIND_BY)) {
            ColumnQuery query = queryParser.parse(methodName, args, classRepresentation);
            Object callBack = args[args.length - 1];
            if (Consumer.class.isInstance(callBack)) {

                Optional<ConsistencyLevel> consistencyLevel = getConsistencyLevel(args);
                if (consistencyLevel.isPresent()) {
                    repository.select(query, consistencyLevel.get(), Consumer.class.cast(callBack));
                } else {
                    repository.select(query, Consumer.class.cast(callBack));
                }
                return null;
            }

            throw new DynamicQueryException("On select async method you must put a java.util.function.Consumer" +
                    " as end parameter as callback");
        }

        if (methodName.startsWith(DELETE_BY)) {
            Object callBack = args[args.length - 1];
            ColumnDeleteQuery query = queryDeleteParser.parse(methodName, args, classRepresentation);
            Optional<ConsistencyLevel> consistencyLevel = getConsistencyLevel(args);
            if (Consumer.class.isInstance(callBack)) {

                if (consistencyLevel.isPresent()) {
                    repository.delete(query, consistencyLevel.get(), Consumer.class.cast(callBack));
                } else {
                    repository.delete(query, Consumer.class.cast(callBack));
                }

                return null;
            }

            if (consistencyLevel.isPresent()) {
                repository.delete(query, consistencyLevel.get());
            } else {
                repository.delete(query);
            }

            return null;
        }

        return null;
    }


    private Optional<ConsistencyLevel> getConsistencyLevel(Object[] args) {
        return Stream.of(args)
                .filter(a -> ConsistencyLevel.class.isInstance(a))
                .map(c -> ConsistencyLevel.class.cast(c))
                .findFirst();
    }


    class ColumnRepositoryAsync extends AbstractColumnRepositoryAsync implements CassandraRepositoryAsync {

        private final CassandraTemplateAsync template;

        private final Reflections reflections;

        private final ClassRepresentation classRepresentation;

        ColumnRepositoryAsync(CassandraTemplateAsync template, ClassRepresentation classRepresentation, Reflections reflections) {
            this.template = template;
            this.classRepresentation = classRepresentation;
            this.reflections = reflections;
        }

        @Override
        protected ColumnTemplateAsync getTemplate() {
            return template;
        }

        @Override
        protected Reflections getReflections() {
            return reflections;
        }

        @Override
        protected ClassRepresentation getClassRepresentation() {
            return classRepresentation;
        }

        @Override
        public void save(Object entity, ConsistencyLevel level) {
            template.save(entity, level);
        }

        @Override
        public void save(Object entity, Duration ttl, ConsistencyLevel level)
                throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            template.save(entity, ttl, level);
        }

        @Override
        public void save(Iterable entities, ConsistencyLevel level) {
            template.save(entities, level);
        }

        @Override
        public void save(Iterable entities, Duration ttl, ConsistencyLevel level)
                throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            template.save(entities, ttl, level);
        }

        @Override
        public void save(Object entity, ConsistencyLevel level, Consumer callBack)
                throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            template.save(entity, level, callBack);
        }

        @Override
        public void save(Object entity, Duration ttl, ConsistencyLevel level, Consumer callBack)
                throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
            template.save(entity, ttl, level, callBack);
        }


    }
}
