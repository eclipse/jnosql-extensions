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
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CDIJUnit4Runner.class)
public class CassandraRepositoryProxyTest {


    private CassandraTemplate template;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    private PersonRepository personRepository;


    @Before
    public void setUp() {
        this.template = Mockito.mock(CassandraTemplate.class);

        CassandraRepositoryProxy handler = new CassandraRepositoryProxy(template,
                classRepresentations, PersonRepository.class, reflections);

        when(template.insert(any(Person.class))).thenReturn(new Person());
        when(template.insert(any(Person.class), any(Duration.class))).thenReturn(new Person());
        when(template.update(any(Person.class))).thenReturn(new Person());
        personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }


    @Test
    public void shouldFindByName() {
        ConsistencyLevel level = ConsistencyLevel.ANY;
        when(template.save(Mockito.any(Person.class), Mockito.eq(level))).thenReturn(new Person());
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        personRepository.findByName("Ada", level);
        verify(template).find(Mockito.any(ColumnQuery.class), Mockito.eq(level));
    }

    @Test
    public void shouldDeleteByName() {
        ConsistencyLevel level = ConsistencyLevel.ANY;
        when(template.save(Mockito.any(Person.class), Mockito.eq(level))).thenReturn(new Person());
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        personRepository.deleteByName("Ada", level);
        verify(template).delete(Mockito.any(ColumnDeleteQuery.class), Mockito.eq(level));
    }

    @Test
    public void shouldFindAll() {
        personRepository.findAll();
        verify(template).cql("select * from Person");
    }

    @Test
    public void shouldFindByNameCQL() {
        personRepository.findByName("Ada");
        verify(template).cql(Mockito.eq("select * from Person where name = ?"), Mockito.any(Object.class));
    }

    @Test
    public void shouldFindByName2CQL() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

        personRepository.findByName2("Ada");
        verify(template).cql(Mockito.eq("select * from Person where name = :name"), captor.capture());
        Map map = captor.getValue();
        Assert.assertEquals("Ada", map.get("name"));
    }

    interface PersonRepository extends CassandraRepository<Person, String> {

        Person findByName(String name, ConsistencyLevel level);

        void deleteByName(String name, ConsistencyLevel level);

        @CQL("select * from Person")
        List<Person> findAll();

        @CQL("select * from Person where name = ?")
        List<Person> findByName(String name);

        @CQL("select * from Person where name = :name")
        List<Person> findByName2(@Param("name") String name);
    }

}