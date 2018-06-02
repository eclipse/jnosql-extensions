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
package org.jnosql.artemis.hazelcast.key;

import com.hazelcast.query.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HazelcastRepositoryProxyTest {

    @Mock
    private HazelcastTemplate template;

    private PersonRepository personRepository;


    @BeforeEach
    public void setUp() {

        Collection<Object> people = asList(new Person("Poliana", 25), new Person("Otavio", 28));

        when(template.sql(anyString())).thenReturn(people);
        when(template.sql(anyString(), any(Map.class))).thenReturn(people);
        when(template.sql(any(Predicate.class))).thenReturn(people);


        HazelcastRepositoryProxy handler = new HazelcastRepositoryProxy(template, PersonRepository.class);

        personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }

    @Test
    public void shouldFindAll() {
        List<Person> people = personRepository.findActive();
        verify(template).sql("active");
        assertNotNull(people);
        assertTrue(people.stream().allMatch(Person.class::isInstance));
    }

    @Test
    public void shouldFindByAgeAndInteger() {
        Set<Person> people = personRepository.findByAgeAndInteger("Ada", 10);
        Map<String, Object> params = new HashMap<>();
        params.put("age", 10);
        params.put("name", "Ada");
        verify(template).sql("name = :name AND age = :age", params);
        assertNotNull(people);
        assertTrue(people.stream().allMatch(Person.class::isInstance));
    }


    interface PersonRepository extends HazelcastRepository<Person, String> {

        @Query("active")
        List<Person> findActive();

        @Query("name = :name AND age = :age")
        Set<Person> findByAgeAndInteger(@Param("name") String name, @Param("age") Integer age);
    }
}