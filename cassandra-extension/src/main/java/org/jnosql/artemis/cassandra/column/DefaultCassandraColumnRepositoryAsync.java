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
package org.jnosql.artemis.cassandra.column;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Statement;
import org.jnosql.artemis.column.AbstractColumnRepositoryAsync;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManagerAsync;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * The CassandraColumnRepositoryAsync default implementation
 */
class DefaultCassandraColumnRepositoryAsync extends AbstractColumnRepositoryAsync
        implements CassandraColumnRepositoryAsync {


    private ColumnEntityConverter converter;

    private Instance<CassandraColumnFamilyManagerAsync> managerAsync;

    DefaultCassandraColumnRepositoryAsync() {
    }

    @Inject
    DefaultCassandraColumnRepositoryAsync(ColumnEntityConverter converter,
                                          Instance<CassandraColumnFamilyManagerAsync> managerAsync) {
        this.converter = converter;
        this.managerAsync = managerAsync;
    }

    @Override
    protected ColumnEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected ColumnFamilyManagerAsync getManager() {
        return managerAsync.get();
    }

    @Override
    public <T> void save(T entity, ConsistencyLevel level) {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(level, "level is required");
        managerAsync.get().save(converter.toColumn(entity), level);
    }

    @Override
    public <T> void save(T entity, ConsistencyLevel level, Consumer<T> callBack)
            throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(level, "level is required");
        Objects.requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        managerAsync.get().save(converter.toColumn(entity), level, dianaCallBack);
    }

    @Override
    public <T> void save(T entity, Duration ttl, ConsistencyLevel level)
            throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(level, "level is required");
        Objects.requireNonNull(ttl, "ttl is required");
        managerAsync.get().save(converter.toColumn(entity), ttl, level);
    }

    @Override
    public <T> void save(Iterable<T> entities, ConsistencyLevel level) {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(level, "level is required");
        StreamSupport.stream(entities.spliterator(), false)
                .map(converter::toColumn)
                .forEach(c -> managerAsync.get().save(c, level));
    }

    @Override
    public <T> void save(Iterable<T> entities, Duration ttl, ConsistencyLevel level)
            throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(level, "level is required");
        Objects.requireNonNull(ttl, "ttl is required");
        StreamSupport.stream(entities.spliterator(), false)
                .map(converter::toColumn)
                .forEach(c -> managerAsync.get().save(c, ttl, level));
    }

    @Override
    public <T> void save(T entity, Duration ttl, ConsistencyLevel level, Consumer<T> callBack)
            throws ExecuteAsyncQueryException, UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(level, "level is required");
        Objects.requireNonNull(ttl, "ttl is required");
        Objects.requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        managerAsync.get().save(converter.toColumn(entity), ttl, level, dianaCallBack);
    }

    @Override
    public <T> void delete(ColumnDeleteQuery query, ConsistencyLevel level) throws NullPointerException {
        managerAsync.get().delete(query, level);
    }

    @Override
    public void delete(ColumnDeleteQuery query, ConsistencyLevel level, Consumer<Void> consumer) {
        managerAsync.get().delete(query, level, consumer);
    }

    @Override
    public <T> void find(ColumnQuery query, ConsistencyLevel level, Consumer<List<T>> callBack)
            throws ExecuteAsyncQueryException, NullPointerException {
        Objects.requireNonNull(callBack, "callBack is required");
        Consumer<List<ColumnEntity>> dianaCallBack = d -> {
            callBack.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };

        managerAsync.get().find(query, level, dianaCallBack);
    }

    @Override
    public <T> void cql(String query, Consumer<List<T>> callBack)
            throws ExecuteAsyncQueryException, NullPointerException {
        Objects.requireNonNull(callBack, "callBack is required");
        Consumer<List<ColumnEntity>> dianaCallBack = d -> {
            callBack.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };
        managerAsync.get().cql(query, dianaCallBack);
    }

    @Override
    public <T> void execute(Statement statement, Consumer<List<T>> callBack)
            throws ExecuteAsyncQueryException, NullPointerException {
        Objects.requireNonNull(callBack, "callBack is required");
        Consumer<List<ColumnEntity>> dianaCallBack = d -> {
            callBack.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };
        managerAsync.get().execute(statement, dianaCallBack);
    }


}
