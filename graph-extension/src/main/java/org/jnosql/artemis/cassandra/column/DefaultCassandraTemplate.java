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
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.column.ColumnEntityConverter;
import jakarta.nosql.mapping.column.ColumnEventPersistManager;
import jakarta.nosql.mapping.column.ColumnWorkflow;
import jakarta.nosql.mapping.reflection.ClassMappings;
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnFamilyManager;
import jakarta.nosql.column.ColumnQuery;
import org.jnosql.artemis.column.AbstractColumnTemplate;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManager;
import org.jnosql.diana.cassandra.column.CassandraPrepareStatment;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Typed(CassandraTemplate.class)
class DefaultCassandraTemplate extends AbstractColumnTemplate implements CassandraTemplate {

    private Instance<CassandraColumnFamilyManager> manager;

    private CassandraColumnEntityConverter converter;

    private CassandraColumnWorkflow flow;

    private ColumnEventPersistManager persistManager;

    private ClassMappings mappings;

    private Converters converters;

    @Inject
    DefaultCassandraTemplate(Instance<CassandraColumnFamilyManager> manager,
                             CassandraColumnEntityConverter converter,
                             CassandraColumnWorkflow flow,
                             ColumnEventPersistManager persistManager,
                             ClassMappings mappings,
                             Converters converters) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
        this.mappings = mappings;
        this.converters = converters;
    }

    DefaultCassandraTemplate() {
    }


    @Override
    protected ColumnEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected ColumnFamilyManager getManager() {
        return manager.get();
    }

    @Override
    protected ColumnWorkflow getFlow() {
        return flow;
    }

    @Override
    protected ColumnEventPersistManager getEventManager() {
        return persistManager;
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
    public <T> T save(T entity, ConsistencyLevel level) {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(level, "level is required");
        UnaryOperator<ColumnEntity> save = e -> manager.get().save(e, level);
        return getFlow().flow(entity, save);
    }

    @Override
    public <T> Iterable<T> save(Iterable<T> entities, Duration ttl, ConsistencyLevel level) {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(ttl, "ttl is required");
        Objects.requireNonNull(level, "level is required");

        return StreamSupport.stream(entities.spliterator(), false)
                .map(converter::toColumn)
                .map(e -> manager.get().save(e, ttl, level))
                .map(converter::toEntity)
                .map(e -> (T) e)
                .collect(Collectors.toList());
    }

    @Override
    public <T> Iterable<T> save(Iterable<T> entities, ConsistencyLevel level) {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(level, "level is required");
        return StreamSupport.stream(entities.spliterator(), false)
                .map(converter::toColumn)
                .map(e -> manager.get().save(e, level))
                .map(converter::toEntity)
                .map(e -> (T) e)
                .collect(Collectors.toList());
    }

    @Override
    public <T> T save(T entity, Duration ttl, ConsistencyLevel level) {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        Objects.requireNonNull(level, "level is required");
        UnaryOperator<ColumnEntity> save = e -> manager.get().save(e, ttl, level);
        return getFlow().flow(entity, save);
    }

    @Override
    public void delete(ColumnDeleteQuery query, ConsistencyLevel level) {
        Objects.requireNonNull(query, "query is required");
        Objects.requireNonNull(level, "level is required");
        persistManager.firePreDeleteQuery(query);
        manager.get().delete(query, level);
    }

    @Override
    public <T> List<T> find(ColumnQuery query, ConsistencyLevel level) {
        Objects.requireNonNull(query, "query is required");
        Objects.requireNonNull(level, "level is required");
        persistManager.firePreQuery(query);

        return manager.get().select(query, level).stream()
                .map(c -> (T) converter.toEntity(c))
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> cql(String query) {
        return manager.get().cql(query).stream()
                .map(c -> (T) converter.toEntity(c))
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> cql(String query, Map<String, Object> values) {
        return manager.get().cql(query, values).stream()
                .map(c -> (T) converter.toEntity(c))
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> cql(String query, Object... params) {
        Objects.requireNonNull(query, "query is required");
        CassandraPrepareStatment cassandraPrepareStatment = manager.get().nativeQueryPrepare(query);
        List<ColumnEntity> entities = cassandraPrepareStatment.bind(params).executeQuery();
        return entities.stream().map(converter::toEntity).map(e -> (T) e).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> execute(Statement statement) {
        return manager.get().execute(statement).stream()
                .map(c -> (T) converter.toEntity(c))
                .collect(Collectors.toList());
    }


}
