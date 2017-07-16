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
import com.datastax.driver.core.Statement;
import org.jnosql.artemis.column.AbstractColumnTemplateAsync;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManagerAsync;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * The CassandraTemplateAsync default implementation
 */
@Typed(CassandraTemplateAsync.class)
class DefaultCassandraTemplateAsync extends AbstractColumnTemplateAsync
        implements CassandraTemplateAsync {


    private CassandraColumnEntityConverter converter;

    private Instance<CassandraColumnFamilyManagerAsync> managerAsync;

    DefaultCassandraTemplateAsync() {
    }

    @Inject
    DefaultCassandraTemplateAsync(CassandraColumnEntityConverter converter,
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
    public <T> void select(ColumnQuery query, ConsistencyLevel level, Consumer<List<T>> callBack)
            throws ExecuteAsyncQueryException, NullPointerException {
        Objects.requireNonNull(callBack, "callBack is required");
        Consumer<List<ColumnEntity>> dianaCallBack = d -> {
            callBack.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };

        managerAsync.get().select(query, level, dianaCallBack);
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
    public <T> void cql(String query, Consumer<List<T>> callBack, Object... params) throws NullPointerException {
        Objects.requireNonNull(query, "callBack is required");
        Objects.requireNonNull(callBack, "callBack is required");
        Objects.requireNonNull(params, "params is required");
        Consumer<List<ColumnEntity>> dianaCallBack = d -> {
            callBack.accept(
                    d.stream()
                            .map(getConverter()::toEntity)
                            .map(o -> (T) o)
                            .collect(toList()));
        };
        managerAsync.get().nativeQueryPrepare(query).bind(params).executeQueryAsync(dianaCallBack);
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
