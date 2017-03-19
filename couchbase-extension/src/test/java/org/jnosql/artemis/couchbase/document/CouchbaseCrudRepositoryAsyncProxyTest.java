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
package org.jnosql.artemis.couchbase.document;

import com.couchbase.client.java.document.json.JsonObject;
import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentations;
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
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(WeldJUnit4Runner.class)
public class CouchbaseCrudRepositoryAsyncProxyTest {


    private CouchbaseDocumentRepositoryAsync repository;

    @Inject
    private ClassRepresentations classRepresentations;

    private PersonAsyncRepository personRepository;


    @Before
    public void setUp() {
        this.repository = Mockito.mock(CouchbaseDocumentRepositoryAsync.class);

        CouchbaseCrudRepositoryAsyncProxy handler = new CouchbaseCrudRepositoryAsyncProxy(repository,
                classRepresentations, PersonAsyncRepository.class);


        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
    }


    @Test
    public void shouldSave() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 12);
        repository.save(person);
        verify(repository).save(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldSaveWithTTl() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 12);
        ;
        repository.save(person, Duration.ofHours(2));
        verify(repository).save(captor.capture(), Mockito.eq(Duration.ofHours(2)));
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldUpdate() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 12);
        ;
        repository.update(person);
        verify(repository).update(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test
    public void shouldSaveItarable() {
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);
        Person person = new Person("Ada", 12);
        ;
        personRepository.save(singletonList(person));
        verify(repository).save(captor.capture());
        Iterable<Person> persons = captor.getValue();
        assertThat(persons, containsInAnyOrder(person));
    }

    @Test
    public void shouldUpdateItarable() {
        ArgumentCaptor<Iterable> captor = ArgumentCaptor.forClass(Iterable.class);
        Person person = new Person("Ada", 12);
        personRepository.update(singletonList(person));
        verify(repository).update(captor.capture());
        Iterable<Person> persons = captor.getValue();
        assertThat(persons, containsInAnyOrder(person));
    }


    @Test(expected = DynamicQueryException.class)
    public void shouldReturnError() {
        personRepository.findByName("Ada");
    }



    @Test
    public void shouldFindNoCallback() {
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        JsonObject params = JsonObject.create().put("name", "Ada");
        personRepository.queryName(params);
    }

    @Test
    public void shouldFindByNameFromN1ql() {
        Consumer<List<Person>> callBack = p -> {
        };

        JsonObject params = JsonObject.create().put("name", "Ada");
        personRepository.queryName(params, callBack);

        verify(repository).n1qlQuery(Mockito.eq("select * from Person where name= $name"), Mockito.eq(params), Mockito.eq(callBack));
    }

    interface PersonAsyncRepository extends CouchbaseCrudRepositoryAsync<Person> {

        Person findByName(String name);


        @N1QL("select * from Person where name= $name")
        void queryName(JsonObject params);

        @N1QL("select * from Person where name= $name")
        void queryName(JsonObject params, Consumer<List<Person>> callBack);
    }
}