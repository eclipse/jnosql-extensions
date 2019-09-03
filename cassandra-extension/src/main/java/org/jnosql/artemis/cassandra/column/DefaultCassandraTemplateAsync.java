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
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnFamilyManagerAsync;
import jakarta.nosql.column.ColumnQuery;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.column.ColumnEntityConverter;
import jakarta.nosql.mapping.reflection.ClassMappings;
import org.jnosql.artemis.column.AbstractColumnTemplateAsync;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManagerAsync;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * The CassandraTemplateAsync default implementation
 */
@Typed(CassandraTemplateAsync.class)
class DefaultCassandraTemplateAsync extends AbstractColumnTemplateAsync implements CassandraTemplateAsync {

    private CassandraColumnEntityConverter converter;

    private Instance<CassandraColumnFamilyManagerAsync> managerAsync;

    private ClassMappings mappings;

    private Converters converters;

    DefaultCassandraTemplateAsync() {
    }

    @Inject
    DefaultCassandraTemplateAsync(CassandraColumnEntityConverter converter,
                                  Instance<CassandraColumnFamilyManagerAsync> managerAsync,
                                  ClassMappings mappings, Converters converters) {
        this.converter = converter;
        this.managerAsync = managerAsync;
        this.mappings = mappings;
        this.converters = converters;
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
    protected ClassMappings getClassMappings() {
        return mappings;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @Override
    public <T> void save(T entity, ConsistencyLevel level) {
        requireNonNull(entity, "entity is required");
        requireNonNull(level, "level is required");
        managerAsync.get().save(converter.toColumn(entity), level);
    }

    @Override
    public <T> void save(T entity, ConsistencyLevel level, Consumer<T> callBack) {
        requireNonNull(entity, "entity is required");
        requireNonNull(level, "level is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        managerAsync.get().save(converter.toColumn(entity), level, dianaCallBack);
    }

    @Override
    public <T> void save(T entity, Duration ttl, ConsistencyLevel level) {
        requireNonNull(entity, "entity is required");
        requireNonNull(level, "level is required");
        requireNonNull(ttl, "ttl is required");
        managerAsync.get().save(converter.toColumn(entity), ttl, level);
    }

    @Override
    public <T> void save(Iterable<T> entities, ConsistencyLevel level) {
        requireNonNull(entities, "entities is required");
        requireNonNull(level, "level is required");
        StreamSupport.stream(entities.spliterator(), false)
                .map(converter::toColumn)
                .forEach(c -> managerAsync.get().save(c, level));
    }

    @Override
    public <T> void save(Iterable<T> entities, Duration ttl, ConsistencyLevel level) {
        requireNonNull(entities, "entities is required");
        requireNonNull(level, "level is required");
        requireNonNull(ttl, "ttl is required");
        StreamSupport.stream(entities.spliterator(), false)
                .map(converter::toColumn)
                .forEach(c -> managerAsync.get().save(c, ttl, level));
    }

    @Override
    public <T> void save(T entity, Duration ttl, ConsistencyLevel level, Consumer<T> callBack) {
        requireNonNull(entity, "entity is required");
        requireNonNull(level, "level is required");
        requireNonNull(ttl, "ttl is required");
        requireNonNull(callBack, "callBack is required");
        Consumer<ColumnEntity> dianaCallBack = c -> callBack.accept((T) getConverter().toEntity(entity.getClass(), c));
        managerAsync.get().save(converter.toColumn(entity), ttl, level, dianaCallBack);
    }

    @Override
    public <T> void delete(ColumnDeleteQuery query, ConsistencyLevel level) {
        managerAsync.get().delete(query, level);
    }

    @Override
    public void delete(ColumnDeleteQuery query, ConsistencyLevel level, Consumer<Void> consumer) {
        managerAsync.get().delete(query, level, consumer);
    }

    @Override
    public <T> void select(ColumnQuery query, ConsistencyLevel level, Consumer<Stream<T>> callBack) {
        requireNonNull(callBack, "callBack is required");
        Consumer<Stream<ColumnEntity>> dianaCallBack = d -> callBack.accept(
                d.map(getConverter()::toEntity)
                        .map(o -> (T) o));

        managerAsync.get().select(query, level, dianaCallBack);
    }

    @Override
    public <T> void cql(String query, Consumer<Stream<T>> callBack) {
        requireNonNull(callBack, "callBack is required");
        Consumer<Stream<ColumnEntity>> dianaCallBack = d -> callBack.accept(
                d.map(getConverter()::toEntity)
                        .map(o -> (T) o));
        managerAsync.get().cql(query, dianaCallBack);
    }

    @Override
    public <T> void cql(String query, Map<String, Object> values, Consumer<Stream<T>> callBack) {
        requireNonNull(callBack, "callBack is required");
        Consumer<Stream<ColumnEntity>> dianaCallBack = d -> callBack.accept(
                d.map(getConverter()::toEntity)
                        .map(o -> (T) o));
        managerAsync.get().cql(query, values, dianaCallBack);
    }

    @Override
    public <T> void cql(String query, Consumer<Stream<T>> callBack, Object... params) {
        requireNonNull(query, "callBack is required");
        requireNonNull(callBack, "callBack is required");
        requireNonNull(params, "params is required");
        Consumer<Stream<ColumnEntity>> dianaCallBack = d -> callBack.accept(
                d.map(getConverter()::toEntity)
                        .map(o -> (T) o));
        managerAsync.get().nativeQueryPrepare(query).bind(params).executeQueryAsync(dianaCallBack);
    }

    @Override
    public <T> void execute(Statement statement, Consumer<Stream<T>> callBack) {
        requireNonNull(callBack, "callBack is required");
        Consumer<Stream<ColumnEntity>> dianaCallBack = d -> callBack.accept(
                d.map(getConverter()::toEntity)
                        .map(o -> (T) o));
        managerAsync.get().execute(statement, dianaCallBack);
    }

}
