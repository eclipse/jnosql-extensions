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

import org.jnosql.artemis.DynamicQueryException;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.column.ColumnQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(WeldJUnit4Runner.class)
public class OrientDBRepositoryAsyncProxyTest {


    private OrientDBDocumentTemplateAsync repository;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    private PersonAsyncRepository personRepository;


    @Before
    public void setUp() {
        this.repository = Mockito.mock(OrientDBDocumentTemplateAsync.class);

        OrientDBRepositoryAsyncProxy handler = new OrientDBRepositoryAsyncProxy(repository,
                classRepresentations, PersonAsyncRepository.class, reflections);


        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
    }



    @Test(expected = DynamicQueryException.class)
    public void shouldReturnError() {
        personRepository.findByName("Ada");
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

        verify(repository).find(Mockito.eq("select * from Person where name= ?"), Mockito.eq(callBack), captor.capture());
        Object value = captor.getValue();
        assertEquals("Ada", value);

    }

    interface PersonAsyncRepository extends OrientDBCrudRepositoryAsync<Person, String> {

        Person findByName(String name);


        @SQL("select * from Person where name= ?")
        void queryName(String name);

        @SQL("select * from Person where name= ?")
        void queryName(String name, Consumer<List<Person>> callBack);
    }
}