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

import jakarta.nosql.mapping.column.ColumnRepositoryAsyncProducer;
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(CDIExtension.class)
public class CassandraRepositoryAsyncProxyTest {


    private CassandraTemplateAsync template;

    @Inject
    private ColumnRepositoryAsyncProducer producer;

    private PersonAsyncRepository personRepository;


    @BeforeEach
    public void setUp() {
        this.template = Mockito.mock(CassandraTemplateAsync.class);
        PersonAsyncRepository asyncRepository = producer.get(PersonAsyncRepository.class, template);
        CassandraRepositoryAsyncProxy handler = new CassandraRepositoryAsyncProxy(template, asyncRepository);

        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
    }


    @Test
    public void shouldSaveWithTTl() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 12);

        template.insert(person, Duration.ofHours(2));
        verify(template).insert(captor.capture(), Mockito.eq(Duration.ofHours(2)));
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldUpdate() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 12);

        template.update(person);
        verify(template).update(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }

    @Test
    public void shouldFindByName() {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        Consumer<List<Person>> callBack = p -> {
        };
        personRepository.findByName("Ada", callBack);

        verify(template).select(captor.capture(), Mockito.eq(callBack));
        ColumnQuery query = captor.getValue();
        assertEquals("Person", query.getColumnFamily());

    }

    @Test
    public void shouldDeleteByName() {
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        Consumer<Void> callBack = p -> {
        };
        personRepository.deleteByName("Ada", callBack);

        verify(template).delete(captor.capture(), Mockito.eq(callBack));
        ColumnDeleteQuery query = captor.getValue();
        assertEquals("Person", query.getColumnFamily());
    }

    @Test
    public void shouldFindNoCallback() {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        personRepository.queryName("Ada");
    }

    @Test
    public void shouldFindByNameFromCQL() {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        Consumer<List<Person>> callBack = p -> {
        };
        personRepository.queryName("Ada", callBack);

        verify(template).cql(Mockito.eq("select * from Person where name= ?"), Mockito.eq(callBack), captor.capture());
        Object value = captor.getValue();
        assertEquals("Ada", value);

    }

    @Test
    public void shouldFindByNameFromCQL2() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        Consumer<List<Person>> callBack = p -> {
        };
        personRepository.queryName2("Ada", callBack);

        verify(template).cql(Mockito.eq("select * from Person where name= :personName"), captor.capture(), Mockito.eq(callBack));
        Map value = captor.getValue();
        assertEquals("Ada", value.get("personName"));

    }

    interface PersonAsyncRepository extends CassandraRepositoryAsync<Person, String> {

        Person findByName(String name);

        Person findByName(String name, Consumer<List<Person>> callBack);

        void deleteByName(String name, Consumer<Void> callBack);

        @CQL("select * from Person where name= ?")
        void queryName(String name);

        @CQL("select * from Person where name= ?")
        void queryName(String name, Consumer<List<Person>> callBack);


        @CQL("select * from Person where name= :personName")
        void queryName2(@Param("personName") String name, Consumer<List<Person>> callBack);
    }


}