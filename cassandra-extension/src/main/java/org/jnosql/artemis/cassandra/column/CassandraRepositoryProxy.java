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
import org.jnosql.artemis.column.ColumnTemplate;
import org.jnosql.artemis.column.query.AbstractColumnRepository;
import org.jnosql.artemis.column.query.ColumnQueryDeleteParser;
import org.jnosql.artemis.column.query.ColumnQueryParser;
import org.jnosql.artemis.column.query.ReturnTypeConverterUtil;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.jnosql.artemis.cassandra.column.CQLObjectUtil.getValues;

class CassandraRepositoryProxy<T> implements InvocationHandler {

    private static final String SAVE = "save";
    private static final String UPDATE = "update";
    private static final String FIND_BY = "findBy";
    private static final String DELETE_BY = "deleteBy";

    private final Class<T> typeClass;

    private final CassandraTemplate repository;

    private final ColumnRepository crudRepository;

    private final ClassRepresentation classRepresentation;

    private final ColumnQueryParser queryParser;

    private final ColumnQueryDeleteParser deleteQueryParser;


    CassandraRepositoryProxy(CassandraTemplate repository, ClassRepresentations classRepresentations, Class<?> repositoryType,
                             Reflections reflections) {

        this.repository = repository;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.crudRepository = new ColumnRepository(repository, classRepresentation, reflections);
        this.queryParser = new ColumnQueryParser();
        this.deleteQueryParser = new ColumnQueryDeleteParser();
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        CQL cql = method.getAnnotation(CQL.class);
        if (Objects.nonNull(cql)) {

            List<T> result;
            Map<String, Object> values = getValues(args, method);
            if (!values.isEmpty()) {
                result = repository.cql(cql.value(), values);
            } else if (args == null || args.length == 0) {
                result = repository.cql(cql.value());
            } else {
                result = repository.cql(cql.value(), args);
            }
            return CassandraReturnTypeConverterUtil.returnObject(result, typeClass, method);
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
            Optional<ConsistencyLevel> consistencyLevel = getConsistencyLevel(args);
            if (consistencyLevel.isPresent()) {
                return CassandraReturnTypeConverterUtil.returnObject(query, repository, typeClass, method,
                        consistencyLevel.get());
            } else {
                return ReturnTypeConverterUtil.returnObject(query, repository, typeClass, method);
            }
        }

        if (methodName.startsWith(DELETE_BY)) {
            Optional<ConsistencyLevel> consistencyLevel = getConsistencyLevel(args);

            ColumnDeleteQuery query = deleteQueryParser.parse(methodName, args, classRepresentation);
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


    class ColumnRepository extends AbstractColumnRepository implements CassandraRepository {

        private final CassandraTemplate template;

        private final ClassRepresentation classRepresentation;

        private final Reflections reflections;

        ColumnRepository(CassandraTemplate template, ClassRepresentation classRepresentation, Reflections reflections) {
            this.template = template;
            this.classRepresentation = classRepresentation;
            this.reflections = reflections;
        }

        @Override
        protected ColumnTemplate getTemplate() {
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

        @Override
        public Object save(Object entity, ConsistencyLevel level) throws NullPointerException {
            return template.save(entity, level);
        }

        @Override
        public Object save(Object entity, Duration ttl, ConsistencyLevel level) throws NullPointerException {
            return template.save(entity, ttl, level);
        }

        @Override
        public Iterable save(Iterable entities, ConsistencyLevel level) throws NullPointerException {
            return template.save(entities, level);
        }

        @Override
        public Iterable save(Iterable entities, Duration ttl, ConsistencyLevel level) throws NullPointerException {
            return template.save(entities, ttl, level);
        }


    }
}
