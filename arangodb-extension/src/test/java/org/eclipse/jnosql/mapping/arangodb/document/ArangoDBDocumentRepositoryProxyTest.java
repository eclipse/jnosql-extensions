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
package org.eclipse.jnosql.mapping.arangodb.document;

import jakarta.nosql.mapping.document.DocumentRepositoryProducer;
import org.eclipse.jnosql.artemis.test.CDIExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@CDIExtension
public class ArangoDBDocumentRepositoryProxyTest {

    private ArangoDBTemplate template;

    @Inject
    private DocumentRepositoryProducer producer;

    private PersonRepository personRepository;

    @BeforeEach
    public void setUp() {
        this.template = Mockito.mock(ArangoDBTemplate.class);

        PersonRepository personRepository = producer.get(PersonRepository.class, template);
        ArangoDBDocumentRepositoryProxy handler = new ArangoDBDocumentRepositoryProxy(template,
                PersonRepository.class, personRepository);

        when(template.insert(any(Person.class))).thenReturn(new Person());
        when(template.insert(any(Person.class), any(Duration.class))).thenReturn(new Person());
        when(template.update(any(Person.class))).thenReturn(new Person());
        this.personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }


    @Test
    public void shouldFindAll() {
        personRepository.findAll();
        verify(template).aql("FOR p IN Person RETURN p", emptyMap());
    }

    @Test
    public void shouldFindByNameAQL() {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        personRepository.findByName("Ada");
        verify(template).aql(eq("FOR p IN Person FILTER p.name = @name RETURN p"), captor.capture());

        Map value = captor.getValue();
        assertEquals("Ada", value.get("name"));
    }

    interface PersonRepository extends ArangoDBRepository<Person, String> {

        @AQL("FOR p IN Person RETURN p")
        List<Person> findAll();

        @AQL("FOR p IN Person FILTER p.name = @name RETURN p")
        List<Person> findByName(@Param("name") String name);
    }
}