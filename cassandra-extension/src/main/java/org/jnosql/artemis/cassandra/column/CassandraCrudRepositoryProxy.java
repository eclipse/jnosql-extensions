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
package org.jnosql.artemis.cassandra.column;


import com.datastax.driver.core.ConsistencyLevel;
import org.jnosql.artemis.column.ColumnRepository;
import org.jnosql.artemis.column.query.AbstractColumnCrudRepository;
import org.jnosql.artemis.column.query.ColumnQueryDeleteParser;
import org.jnosql.artemis.column.query.ColumnQueryParser;
import org.jnosql.artemis.column.query.ReturnTypeConverterUtil;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

class CassandraCrudRepositoryProxy<T> implements InvocationHandler {

    private static final String SAVE = "save";
    private static final String UPDATE = "update";
    private static final String FIND_BY = "findBy";
    private static final String DELETE_BY = "deleteBy";

    private final Class<T> typeClass;

    private final CassandraColumnRepository repository;

    private final ColumnCrudRepository crudRepository;

    private final ClassRepresentation classRepresentation;

    private final ColumnQueryParser queryParser;

    private final ColumnQueryDeleteParser deleteQueryParser;


    CassandraCrudRepositoryProxy(CassandraColumnRepository repository, ClassRepresentations classRepresentations, Class<?> repositoryType) {
        this.repository = repository;
        this.crudRepository = new ColumnCrudRepository(repository);
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new ColumnQueryParser();
        this.deleteQueryParser = new ColumnQueryDeleteParser();
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        CQL cql = method.getAnnotation(CQL.class);
        if (Objects.nonNull(cql)) {
            List<T> result = Collections.emptyList();
            if (args == null || args.length == 0) {
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


    class ColumnCrudRepository extends AbstractColumnCrudRepository implements CassandraCrudRepository {

        private final CassandraColumnRepository repository;

        ColumnCrudRepository(CassandraColumnRepository repository) {
            this.repository = repository;
        }

        @Override
        protected ColumnRepository getColumnRepository() {
            return repository;
        }

        @Override
        public Object save(Object entity, ConsistencyLevel level) throws NullPointerException {
            return repository.save(entity, level);
        }

        @Override
        public Object save(Object entity, Duration ttl, ConsistencyLevel level) throws NullPointerException {
            return repository.save(entity, ttl, level);
        }

        @Override
        public Iterable save(Iterable entities, ConsistencyLevel level) throws NullPointerException {
            return repository.save(entities, level);
        }

        @Override
        public Iterable save(Iterable entities, Duration ttl, ConsistencyLevel level) throws NullPointerException {
            return repository.save(entities, ttl, level);
        }
    }
}
