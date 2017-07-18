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
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(WeldJUnit4Runner.class)
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
        verify(template).sql("sql * from Person");
    }

    @Test
    public void shouldFindByNameCQL() {
        personRepository.findByName("Ada");
        verify(template).sql(Mockito.eq("sql * from Person where name = ?"), Mockito.any());
    }

    interface PersonRepository extends OrientDBCrudRepository<Person, String> {

        @SQL("sql * from Person")
        List<Person> findAll();

        @SQL("sql * from Person where name = ?")
        List<Person> findByName(String name);
    }
}