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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(CDIJUnitRunner.class)
public class HazelcastRepositoryProxyTest {

    @Inject
    private HazelcastTemplate template;

    private PersonRepository personRepository;


    @Before
    public void setUp() {
        HazelcastRepositoryProxy handler = new HazelcastRepositoryProxy(template, PersonRepository.class);

        personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }

    @Test
    public void shouldFindAll() {
        List<Person> people = personRepository.findActive();
        verify(template).query("active");
        assertNotNull(people);
        assertTrue(people.stream().allMatch(Person.class::isInstance));
    }


    interface PersonRepository extends HazelcastRepository<Person, String> {

        @Query("active")
        List<Person> findActive();

        @Query("name = :name AND age = :age")
        List<Person> findByAgeAndInteger(@Param("name") String name, Integer age);
    }
}