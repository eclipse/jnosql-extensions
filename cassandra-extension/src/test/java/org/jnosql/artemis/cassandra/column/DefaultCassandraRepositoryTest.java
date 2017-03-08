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
import org.hamcrest.Matchers;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.artemis.column.ColumnWorkflow;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.api.column.ColumnQuery;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(WeldJUnit4Runner.class)
public class DefaultCassandraRepositoryTest {

    @Inject
    private ColumnEntityConverter converter;

    @Inject
    private ColumnWorkflow flow;

    private CassandraRepository repository;

    private CassandraColumnFamilyManager manager;

    @Before
    public void setUp() {
        this.manager = mock(CassandraColumnFamilyManager.class);
        Instance instance = mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        repository = new DefaultCassandraRepository(instance, converter, flow);
    }

    @Test
    public void shouldSaveConsntency() {
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));

        ArgumentCaptor<ColumnEntity> captor = ArgumentCaptor.forClass(ColumnEntity.class);

        ConsistencyLevel level = ConsistencyLevel.THREE;

        when(manager.
                save(Mockito.any(ColumnEntity.class), Mockito.eq(level)))
                .thenReturn(entity);

        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        assertEquals(person, repository.save(person, level));

        Mockito.verify(manager).save(captor.capture(), Mockito.eq(level));
        assertEquals(entity, captor.getValue());

    }

    @Test
    public void shouldSaveConsntencyDuration() {
        Duration duration = Duration.ofHours(2);
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));

        ArgumentCaptor<ColumnEntity> captor = ArgumentCaptor.forClass(ColumnEntity.class);

        ConsistencyLevel level = ConsistencyLevel.THREE;
        when(manager.
                save(Mockito.any(ColumnEntity.class), Mockito.eq(duration),
                        Mockito.eq(level)))
                .thenReturn(entity);

        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        assertEquals(person, repository.save(person, duration, level));

        Mockito.verify(manager).save(captor.capture(), Mockito.eq(duration), Mockito.eq(level));
        assertEquals(entity, captor.getValue());
    }

    @Test
    public void shouldDelete() {
        ColumnDeleteQuery query = ColumnDeleteQuery.of("");
        ConsistencyLevel level = ConsistencyLevel.THREE;
        repository.delete(query, level);
        verify(manager).delete(query, level);
    }


    @Test
    public void shouldFind() {
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);

        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));
        ColumnQuery query = ColumnQuery.of("");
        ConsistencyLevel level = ConsistencyLevel.THREE;
        when(manager.find(query, level)).thenReturn(Collections.singletonList(entity));

        List<Person> people = repository.find(query, level);
        Assert.assertThat(people, Matchers.contains(person));
    }

    @Test
    public void shouldFindCQL() {
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        String cql = "select * from Person";
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));

        when(manager.cql(cql)).thenReturn(Collections.singletonList(entity));

        List<Person> people = repository.cql(cql);
        Assert.assertThat(people, Matchers.contains(person));
    }

    @Test
    public void shouldFindStatment() {
        Statement statement = QueryBuilder.select().from("Person");
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));

        when(manager.execute(statement)).thenReturn(Collections.singletonList(entity));

        List<Person> people = repository.execute(statement);
        Assert.assertThat(people, Matchers.contains(person));
    }

}