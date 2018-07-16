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
import org.jnosql.artemis.document.DocumentRepositoryAsyncProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(CDIExtension.class)
public class OrientDBRepositoryAsyncProxyTest {


    private OrientDBTemplateAsync templateAsync;

    @Inject
    private DocumentRepositoryAsyncProducer producer;

    private PersonAsyncRepository personRepository;


    @BeforeEach
    public void setUp() {
        this.templateAsync = Mockito.mock(OrientDBTemplateAsync.class);

        PersonAsyncRepository personAsyncRepository = producer.get(PersonAsyncRepository.class, templateAsync);
        OrientDBRepositoryAsyncProxy handler = new OrientDBRepositoryAsyncProxy(templateAsync, personAsyncRepository);


        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
    }



    @Test
    public void shouldReturnError() {
        assertThrows(DynamicQueryException.class, () -> personRepository.findByName("Ada"));
    }



    @Test
    public void shouldFindNoCallback() {
        personRepository.queryName("Ada");
    }

    @Test
    public void shouldFindByNameFromSQL() {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        Consumer<List<Person>> callBack = p -> {
        };
        personRepository.queryName("Ada", callBack);

        verify(templateAsync).sql(Mockito.eq("select * from Person where name= ?"), Mockito.eq(callBack), captor.capture());
        Object value = captor.getValue();
        assertEquals("Ada", value);

    }

    @Test
    public void shouldFindByNameFromSQL2() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        Consumer<List<Person>> callBack = p -> {
        };
        personRepository.findByAge(10, callBack);

        verify(templateAsync).sql(Mockito.eq("select * from Person where age= :age"), Mockito.eq(callBack), captor.capture());
        Map value = captor.getValue();
        assertEquals(10, value.get("age"));

    }

    interface PersonAsyncRepository extends OrientDBCrudRepositoryAsync<Person, String> {

        Person findByName(String name);


        @SQL("select * from Person where name= ?")
        void queryName(String name);

        @SQL("select * from Person where name= ?")
        void queryName(String name, Consumer<List<Person>> callBack);

        @SQL("select * from Person where age= :age")
        void findByAge(@Param("age") Integer age, Consumer<List<Person>> callBack);
    }
}