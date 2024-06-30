/*
 *  Copyright (c) 2024 Contributors to the Eclipse Foundation
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
 *   Ondro Mihalyi
 */
package ee.omnifish.jnosql.jakartapersistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersonRepositoryTest {

    private SeContainer cdiContainer;
    private PersonRepository personRepo;

    @BeforeEach
    void init() {
        cdiContainer = SeContainerInitializer.newInstance()
                .addBeanClasses(EntityManagerProducer.class)
                .initialize();
        assertThat("repository can be resolved", cdiContainer.select(PersonRepository.class).isResolvable());
        personRepo = cdiContainer.select(PersonRepository.class).get();
        getEntityManager().getTransaction().begin();
    }

    private EntityManager getEntityManager() {
        return cdiContainer.select(EntityManager.class).get();
    }

    @AfterEach
    void cleanup() {
        getEntityManager().getTransaction().commit();
        cdiContainer.close();
    }

    @Test
    void findAll() {
        final List<Person> persons = personRepo.findAll().toList();
        assertThat("queryResult", persons, is(empty()));
        System.out.println("All persons: " + persons);
    }

    @Test
    void count() {
        personRepo.insert(new Person());
        final long count = personRepo.countAll();
        assertThat(count, greaterThan(0L));
    }

    @Test
    void countByNotNull() {
        final Person person = new Person();
        person.setName("Jakarta");
        personRepo.insert(person);
        final long count = personRepo.countByNameNotNull();
        assertThat(count, greaterThan(0L));
    }

    @Test
    void findByXAndYLessThanEqual() {
        final Person person = new Person();
        final String NAME = "Jakarta";
        person.setName(NAME);
        person.setAge(35);
        personRepo.insert(person);

        final List<Person> persons = personRepo.findByNameAndAgeLessThanEqual(NAME, 50);
        assertThat(persons, is(not(empty())));
    }

}
