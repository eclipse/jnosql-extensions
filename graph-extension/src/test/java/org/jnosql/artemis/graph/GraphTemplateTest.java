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
package org.jnosql.artemis.graph;

import org.jnosql.artemis.EntityNotFoundException;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.jnosql.artemis.graph.model.Animal;
import org.jnosql.artemis.graph.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Optional;

import static org.jnosql.artemis.graph.model.Person.builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(WeldJUnit4Runner.class)
public class GraphTemplateTest {

    @Inject
    private GraphTemplate graphTemplate;


    @Test(expected = IdNotFoundException.class)
    public void shouldReturnErrorWhenThereIsNotId() {
        Animal lion = new Animal("lion");
        graphTemplate.insert(lion);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenEntityIsNull() {
        graphTemplate.insert(null);
    }


    @Test
    public void shouldInsertAnEntity() {
        Person person = builder().withAge()
                .withName("Otavio").build();
        Person updated = graphTemplate.insert(person);

        assertNotNull(updated.getId());
    }


    @Test(expected = NullPointerException.class)
    public void shouldGetErrorWhenIdIsNullWhenUpdate() {
        Person person = builder().withAge()
                .withName("Otavio").build();
        graphTemplate.update(person);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldGetErrorWhenEntityIsNotSavedYet() {
        Person person = builder().withAge()
                .withId(10L)
                .withName("Otavio").build();

        graphTemplate.update(person);
    }

    @Test
    public void shouldUpdate() {
        Person person = builder().withAge()
                .withName("Otavio").build();
        Person updated = graphTemplate.insert(person);
        Person newPerson = builder()
                .withAge()
                .withId(updated.getId())
                .withName("Otavio Updated").build();

        Person update = graphTemplate.update(newPerson);

        assertEquals(newPerson, update);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorInFindWhenLabelIsNull() {
        graphTemplate.find(null, 10L);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorInFindWhenIdIsNull() {
        graphTemplate.find("Person", null);
    }

    @Test
    public void shouldFindAnEntity() {
        Person person = builder().withAge()
                .withName("Otavio").build();
        Person updated = graphTemplate.insert(person);
        Optional<Person> personFound = graphTemplate.find("Person", updated.getId());

        assertTrue(personFound.isPresent());
        assertEquals(updated, personFound.get());
    }

    @Test
    public void shouldNotFindAnEntity() {
        Optional<Person> personFound = graphTemplate.find("Person", 0L);
        assertFalse(personFound.isPresent());
    }

    @Test
    public void shouldDeleteAnEntity() {

        Person person = graphTemplate.insert(builder().withAge()
                .withName("Otavio").build());

        assertTrue(graphTemplate.find("Person", person.getId()).isPresent());
        graphTemplate.delete("Person", person.getId());
        assertFalse(graphTemplate.find("Person", person.getId()).isPresent());

    }

    @Test
    public void shouldCreateEdge() {

    }

}