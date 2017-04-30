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
package org.jnosql.artemis.couchbase.document;

import com.couchbase.client.java.document.json.JsonObject;
import org.jnosql.artemis.reflection.ClassRepresentations;
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
public class CouchbaseocumentRepositoryProxyTest {

    private CouchbaseTemplate repository;

    @Inject
    private ClassRepresentations classRepresentations;

    private PersonRepository personRepository;


    @Before
    public void setUp() {
        this.repository = Mockito.mock(CouchbaseTemplate.class);

        CouchbaseocumentRepositoryProxy handler = new CouchbaseocumentRepositoryProxy(repository,
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
    public void shouldFindAll() {
        personRepository.findAll();
        verify(repository).n1qlQuery("select * from Person");
    }

    @Test
    public void shouldFindByNameN1ql() {
        JsonObject params = JsonObject.create().put("name", "Ada");
        personRepository.findByName(params);
        verify(repository).n1qlQuery(Mockito.eq("select * from Person where name = $name"), Mockito.any());
    }

    interface PersonRepository extends CouchbaseRepository<Person> {

        @N1QL("select * from Person")
        List<Person> findAll();

        @N1QL("select * from Person where name = $name")
        List<Person> findByName(JsonObject params);
    }
}