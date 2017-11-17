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
package org.jnosql.artemis.orientdb.document;

import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(CDIJUnitRunner.class)
public class OrientDBDocumentRepositoryProxyTest {

    private OrientDBTemplate template;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    private PersonRepository personRepository;


    @Before
    public void setUp() {
        this.template = Mockito.mock(OrientDBTemplate.class);

        OrientDBDocumentRepositoryProxy handler = new OrientDBDocumentRepositoryProxy(template,
                classRepresentations, PersonRepository.class, reflections);

        when(template.insert(any(Person.class))).thenReturn(new Person());
        when(template.insert(any(Person.class), any(Duration.class))).thenReturn(new Person());
        when(template.update(any(Person.class))).thenReturn(new Person());
        personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }



    @Test
    public void shouldFindAll() {
        personRepository.findAll();
        verify(template).sql("select * from Person");
    }

    @Test
    public void shouldFindByNameSQL() {
        personRepository.findByName("Ada");
        verify(template).sql(Mockito.eq("select * from Person where name = ?"), Mockito.any(Object[].class));
    }

    @Test
    public void shouldFindByNameSQL2() {
        personRepository.findByAge(10);
        ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(template).sql(Mockito.eq("select * from Person where age = :age"), argumentCaptor.capture());
        Map value = argumentCaptor.getValue();
        assertEquals(10, value.get("age"));
    }

    interface PersonRepository extends OrientDBCrudRepository<Person, String> {

        @SQL("select * from Person")
        List<Person> findAll();

        @SQL("select * from Person where name = ?")
        List<Person> findByName(String name);

        @SQL("select * from Person where age = :age")
        List<Person> findByAge(@Param("age") Integer age);
    }
}