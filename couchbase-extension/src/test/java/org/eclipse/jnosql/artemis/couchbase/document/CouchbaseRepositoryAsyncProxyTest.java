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
package org.eclipse.jnosql.artemis.couchbase.document;

import com.couchbase.client.java.document.json.JsonObject;
import jakarta.nosql.mapping.document.DocumentRepositoryAsyncProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(CDIExtension.class)
public class CouchbaseRepositoryAsyncProxyTest {


    private CouchbaseTemplateAsync template;

    @Inject
    private DocumentRepositoryAsyncProducer producer;

    private PersonAsyncRepository personRepository;


    @BeforeEach
    public void setUp() {
        this.template = Mockito.mock(CouchbaseTemplateAsync.class);
        PersonAsyncRepository personAsyncRepository = producer.get(PersonAsyncRepository.class, template);
        CouchbaseRepositoryAsyncProxy handler = new CouchbaseRepositoryAsyncProxy(template, personAsyncRepository);


        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
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
    public void shouldFindNoCallback() {
        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        JsonObject params = JsonObject.create().put("name", "Ada");
        personRepository.queryName("Ada");
        verify(template).n1qlQuery(Mockito.eq("select * from Person where name= $name"), captor.capture(),
                any(Consumer.class));

        JsonObject value = captor.getValue();
        assertEquals("Ada", value.getString("name"));
    }

    @Test
    public void shouldFindByNameFromN1ql() {
        Consumer<Stream<Person>> callBack = p -> {
        };

        JsonObject params = JsonObject.create().put("name", "Ada");
        personRepository.queryName("Ada", callBack);

        verify(template).n1qlQuery(Mockito.eq("select * from Person where name= $name"),
                Mockito.eq(params), Mockito.eq(callBack));

    }

    interface PersonAsyncRepository extends CouchbaseRepositoryAsync<Person, String> {

        Person findByName(String name);


        @N1QL("select * from Person where name= $name")
        void queryName(@Param("name") String name);

        @N1QL("select * from Person where name= $name")
        void queryName(@Param("name") String name, Consumer<Stream<Person>> callBack);
    }
}