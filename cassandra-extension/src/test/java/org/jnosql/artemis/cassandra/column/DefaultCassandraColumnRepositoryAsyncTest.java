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
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManagerAsync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(WeldJUnit4Runner.class)
public class DefaultCassandraColumnRepositoryAsyncTest {

    @Inject
    private CassandraColumnEntityConverter converter;

    private CassandraColumnFamilyManagerAsync managerAsync;

    private CassandraColumnRepositoryAsync repository;

    @Before
    public void setUp() {
        managerAsync = Mockito.mock(CassandraColumnFamilyManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(managerAsync);
        repository = new DefaultCassandraColumnRepositoryAsync(converter, instance);
    }

    @Test
    public void shouldSave() {
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));

        ArgumentCaptor<ColumnEntity> captor = ArgumentCaptor.forClass(ColumnEntity.class);

        ConsistencyLevel level = ConsistencyLevel.THREE;
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        repository.save(person, level);
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
        repository.save(person, level, callBack);
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
        repository.save(person, duration, level);
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
        repository.save(person, duration, level, callBack);
        verify(managerAsync).save(captor.capture(), eq(duration), eq(level), any());
        assertEquals(entity, captor.getValue());
    }

    @Test
    public void shouldDelete() {
        ColumnDeleteQuery query = ColumnDeleteQuery.of("");
        ConsistencyLevel level = ConsistencyLevel.THREE;

        repository.delete(query, level);
        verify(managerAsync).delete(query, level);
    }

    @Test
    public void shouldDeleteCallback() {
        ColumnDeleteQuery query = ColumnDeleteQuery.of("");
        ConsistencyLevel level = ConsistencyLevel.THREE;
        Consumer<Void> callBack = person -> {
        };

        repository.delete(query, level, callBack);
        verify(managerAsync).delete(query, level, callBack);
    }

    @Test
    public void shouldFind() {
        ColumnQuery query = ColumnQuery.of("");
        ConsistencyLevel level = ConsistencyLevel.THREE;
        Consumer<List<Person>> callBack = people -> {
        };
        repository.find(query, level, callBack);
        verify(managerAsync).find(eq(query), eq(level), any());
    }

    @Test
    public void shouldFindCQL() {
        String cql = "select * from Person ";
        Consumer<List<Person>> callBack = people -> {
        };
        repository.cql(cql, callBack);
        verify(managerAsync).cql(eq(cql), any());
    }

    @Test
    public void shouldExecute() {
        Statement statement = QueryBuilder.select().from("Person");
        Consumer<List<Person>> callBack = people -> {
        };
        repository.execute(statement, callBack);
        verify(managerAsync).execute(eq(statement), any());
    }
}