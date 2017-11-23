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
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.hamcrest.Matchers;
import org.jnosql.artemis.column.ColumnEventPersistManager;
import org.jnosql.artemis.column.ColumnWorkflow;
import org.jnosql.artemis.reflection.ClassRepresentations;
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
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.delete;
import static org.jnosql.diana.api.column.query.ColumnQueryBuilder.select;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CDIJUnitRunner.class)
public class DefaultCassandraTemplateTest {

    @Inject
    private CassandraColumnEntityConverter converter;

    @Inject
    private ColumnWorkflow flow;

    @Inject
    private ColumnEventPersistManager persistManager;

    @Inject
    private ClassRepresentations classRepresentations;

    private CassandraTemplate template;

    private CassandraColumnFamilyManager manager;

    @Before
    public void setUp() {
        this.manager = mock(CassandraColumnFamilyManager.class);
        Instance instance = mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultCassandraTemplate(instance, converter, flow, persistManager, classRepresentations);
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
        assertEquals(person, template.save(person, level));

        Mockito.verify(manager).save(captor.capture(), Mockito.eq(level));
        assertEquals(entity, captor.getValue());

    }

    @Test
    public void shouldSaveConsntencyIterable() {
        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));

        ArgumentCaptor<ColumnEntity> captor = ArgumentCaptor.forClass(ColumnEntity.class);

        ConsistencyLevel level = ConsistencyLevel.THREE;

        when(manager.
                save(Mockito.any(ColumnEntity.class), Mockito.eq(level)))
                .thenReturn(entity);

        Person person = new Person();
        person.setName("Name");
        person.setAge(20);
        assertThat(template.save(Collections.singletonList(person), level), Matchers.contains(person));
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
        assertEquals(person, template.save(person, duration, level));

        Mockito.verify(manager).save(captor.capture(), Mockito.eq(duration), Mockito.eq(level));
        assertEquals(entity, captor.getValue());
    }

    @Test
    public void shouldSaveConsntencyDurationIterable() {
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
        assertThat(template.save(Collections.singletonList(person), duration, level), Matchers.contains(person));
        Mockito.verify(manager).save(captor.capture(), Mockito.eq(duration), Mockito.eq(level));
        assertEquals(entity, captor.getValue());
    }

    @Test
    public void shouldDelete() {


        ColumnDeleteQuery query = delete().from("columnFamily").build();
        ConsistencyLevel level = ConsistencyLevel.THREE;
        template.delete(query, level);
        verify(manager).delete(query, level);
    }


    @Test
    public void shouldFind() {
        Person person = new Person();
        person.setName("Name");
        person.setAge(20);

        ColumnEntity entity = ColumnEntity.of("Person", asList(Column.of("name", "Name"), Column.of("age", 20)));
        ColumnQuery query = select().from("columnFamily").build();
        ConsistencyLevel level = ConsistencyLevel.THREE;
        when(manager.select(query, level)).thenReturn(Collections.singletonList(entity));

        List<Person> people = template.find(query, level);
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

        List<Person> people = template.cql(cql);
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

        List<Person> people = template.execute(statement);
        Assert.assertThat(people, Matchers.contains(person));
    }

}