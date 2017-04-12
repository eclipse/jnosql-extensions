/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.cassandra.column;

import com.datastax.driver.core.ConsistencyLevel;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.column.ColumnDeleteQuery;
import org.jnosql.diana.api.column.ColumnQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(WeldJUnit4Runner.class)
public class CassandraCrudRepositoryProxyTest {


    private CassandraColumnRepository repository;

    @Inject
    private ClassRepresentations classRepresentations;

    private PersonRepository personRepository;


    @Before
    public void setUp() {
        this.repository = Mockito.mock(CassandraColumnRepository.class);

        CassandraCrudRepositoryProxy handler = new CassandraCrudRepositoryProxy(repository,
                classRepresentations, PersonRepository.class);

        when(repository.save(any(Person.class))).thenReturn(new Person());
        when(repository.save(any(Person.class), any(Duration.class))).thenReturn(new Person());
        when(repository.update(any(Person.class))).thenReturn(new Person());
        personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }


    @Test
    public void shouldSave() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 20);
        assertNotNull(personRepository.save(person));
        verify(repository).save(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldSaveWithTTl() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 20);
        assertNotNull(personRepository.save(person, Duration.ofHours(2)));
        verify(repository).save(captor.capture(), Mockito.eq(Duration.ofHours(2)));
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldUpdate() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 20);
        assertNotNull(personRepository.update(person));
        verify(repository).update(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldSaveItarable() {
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);
        Person person = new Person("Ada", 20);
        personRepository.save(singletonList(person));
        verify(repository).save(captor.capture());
        Iterable<Person> persons = captor.getValue();
        assertThat(persons, containsInAnyOrder(person));
    }

    @Test
    public void shouldUpdateItarable() {
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);
        Person person = new Person("Ada", 20);
        personRepository.update(singletonList(person));
        verify(repository).update(captor.capture());
        Iterable<Person> persons = captor.getValue();
        assertThat(persons, containsInAnyOrder(person));
    }

    @Test
    public void shouldFindByName() {
        ConsistencyLevel level = ConsistencyLevel.ANY;
        when(repository.save(Mockito.any(Person.class), Mockito.eq(level))).thenReturn(new Person());
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        personRepository.findByName("Ada", level);
        verify(repository).find(Mockito.any(ColumnQuery.class), Mockito.eq(level));
    }

    @Test
    public void shouldDeleteByName() {
        ConsistencyLevel level = ConsistencyLevel.ANY;
        when(repository.save(Mockito.any(Person.class), Mockito.eq(level))).thenReturn(new Person());
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        personRepository.deleteByName("Ada", level);
        verify(repository).delete(Mockito.any(ColumnDeleteQuery.class), Mockito.eq(level));
    }

    @Test
    public void shouldFindAll() {
        personRepository.findAll();
        verify(repository).cql("select * from Person");
    }

    @Test
    public void shouldFindByNameCQL() {
        personRepository.findByName("Ada");
        verify(repository).cql(Mockito.eq("select * from Person where name = ?"), Mockito.any());
    }

    interface PersonRepository extends CassandraCrudRepository<Person> {

        Person findByName(String name, ConsistencyLevel level);

        void deleteByName(String name, ConsistencyLevel level);

        @CQL("select * from Person")
        List<Person> findAll();

        @CQL("select * from Person where name = ?")
        List<Person> findByName(String name);
    }

}