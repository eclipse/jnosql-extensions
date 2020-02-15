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
package org.eclipse.jnosql.artemis.cassandra.column;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import jakarta.nosql.column.Column;
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnQuery;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.reflection.ClassMappings;
import org.eclipse.jnosql.artemis.test.CDIExtension;
import org.eclipse.jnosql.diana.cassandra.column.CassandraColumnFamilyManagerAsync;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static jakarta.nosql.column.ColumnDeleteQuery.delete;
import static jakarta.nosql.column.ColumnQuery.select;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@CDIExtension
public class DefaultCassandraTemplateAsyncTest {

    @Inject
    private CassandraColumnEntityConverter converter;

    @Inject
    private ClassMappings mappings;

    @Inject
    private Converters converters;

    private CassandraColumnFamilyManagerAsync managerAsync;

    private CassandraTemplateAsync templateAsync;

    @BeforeEach
    public void setUp() {
        managerAsync = Mockito.mock(CassandraColumnFamilyManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(managerAsync);
        templateAsync = new DefaultCassandraTemplateAsync(converter, instance, mappings, converters);
    }

    @Test
    public void shouldSave() {
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));

        ArgumentCaptor<ColumnEntity> captor = ArgumentCaptor.forClass(ColumnEntity.class);

        ConsistencyLevel level = ConsistencyLevel.THREE;
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        templateAsync.save(person, level);
        verify(managerAsync).save(captor.capture(), eq(level));
        assertEquals(entity, captor.getValue());
    }

    @Test
    public void shouldSaveCallBack() {
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));

        ArgumentCaptor<ColumnEntity> captor = ArgumentCaptor.forClass(ColumnEntity.class);
        Consumer<Person> callBack = person -> {
        };

        ConsistencyLevel level = ConsistencyLevel.THREE;
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        templateAsync.save(person, level, callBack);
        verify(managerAsync).save(captor.capture(), eq(level), any());
        assertEquals(entity, captor.getValue());
    }

    @Test
    public void shouldSaveDuration() {
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));
        Duration duration = Duration.ofHours(2L);
        ArgumentCaptor<ColumnEntity> captor = ArgumentCaptor.forClass(ColumnEntity.class);

        ConsistencyLevel level = ConsistencyLevel.THREE;
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        templateAsync.save(person, duration, level);
        verify(managerAsync).save(captor.capture(), eq(duration), eq(level));
        assertEquals(entity, captor.getValue());
    }

    @Test
    public void shouldSaveDurationCallBack() {
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));
        Duration duration = Duration.ofHours(2L);
        ArgumentCaptor<ColumnEntity> captor = ArgumentCaptor.forClass(ColumnEntity.class);

        ConsistencyLevel level = ConsistencyLevel.THREE;
        Consumer<Person> callBack = person -> {
        };
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        templateAsync.save(person, duration, level, callBack);
        verify(managerAsync).save(captor.capture(), eq(duration), eq(level), any());
        assertEquals(entity, captor.getValue());
    }

    @Test
    public void shouldDelete() {

        ColumnDeleteQuery query = delete().from("columnFamily").build();
        ConsistencyLevel level = ConsistencyLevel.THREE;

        templateAsync.delete(query, level);
        verify(managerAsync).delete(query, level);
    }

    @Test
    public void shouldDeleteCallback() {
        ColumnDeleteQuery query = delete().from("columnFamily").build();
        ConsistencyLevel level = ConsistencyLevel.THREE;
        Consumer<Void> callBack = person -> {
        };

        templateAsync.delete(query, level, callBack);
        verify(managerAsync).delete(query, level, callBack);
    }

    @Test
    public void shouldFind() {

        ColumnQuery query = select().from("columnFamily").build();
        ConsistencyLevel level = ConsistencyLevel.THREE;
        Consumer<Stream<Person>> callBack = people -> {
        };
        templateAsync.select(query, level, callBack);
        verify(managerAsync).select(eq(query), eq(level), any());
    }

    @Test
    public void shouldFindCQL() {
        String cql = "select * from Person ";
        Consumer<Stream<Person>> callBack = people -> {
        };
        templateAsync.cql(cql, callBack);
        verify(managerAsync).cql(eq(cql), any());
    }

    @Test
    public void shouldExecute() {
        Statement statement = QueryBuilder.select().from("Person");
        Consumer<Stream<Person>> callBack = people -> {
        };
        templateAsync.execute(statement, callBack);
        verify(managerAsync).execute(eq(statement), any());
    }
}