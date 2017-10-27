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
package org.jnosql.artemis.arangodb.document;

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(WeldJUnit4Runner.class)
public class ArangoDBRepositoryAsyncProxyTest {


    private ArangoDBTemplateAsync template;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    private PersonAsyncRepository personRepository;


    @Before
    public void setUp() {
        this.template = Mockito.mock(ArangoDBTemplateAsync.class);

        ArangoDBRepositoryAsyncProxy handler = new ArangoDBRepositoryAsyncProxy(template,
                classRepresentations, PersonAsyncRepository.class, reflections);


        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
    }



    @Test
    public void shouldUpdate() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 12);
        ;
        template.update(person);
        verify(template).update(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test(expected = DynamicQueryException.class)
    public void shouldReturnError() {
        personRepository.findByName("Ada");
    }


    @Test
    public void shouldFindNoCallback() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        Map params = Collections.singletonMap("name", "Ada");
        personRepository.queryName("Ada");
        verify(template).aql(Mockito.eq("select * from Person where name= $name"), captor.capture(),
                any(Consumer.class));

        Map value = captor.getValue();
        assertEquals("Ada", value.get("name"));
    }

    @Test
    public void shouldFindByNameFromAQL() {
        Consumer<List<Person>> callBack = p -> {
        };

        Map params = Collections.singletonMap("name", "Ada");
        personRepository.queryName("Ada", callBack);

        verify(template).aql(Mockito.eq("select * from Person where name= $name"), Mockito.eq(params), Mockito.eq(callBack));

    }

    interface PersonAsyncRepository extends ArangoDBRepositoryAsync<Person, String> {

        Person findByName(String name);


        @AQL("FOR p IN Person RETURN p")
        void queryName(@Param("name") String name);

        @AQL("FOR p IN Person FILTER p.name = @name RETURN p")
        void queryName(@Param("name") String name, Consumer<List<Person>> callBack);
    }
}