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
import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.jnosql.artemis.graph.model.Book;
import org.jnosql.artemis.graph.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(WeldJUnit4Runner.class)
public class EdgeEntityTest {


    @Inject
    private GraphTemplate graphTemplate;


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenInboudIsNull() {
        Person person = Person.builder().withName("Poliana").withAge().build();
        Book book = null;
        graphTemplate.edge(person, "reads", book);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenOutboudIsNull() {
        Person person = Person.builder().withName("Poliana").withAge().build();
        Book book = Book.builder().withAge(2007).withName("The Shack").build();
        graphTemplate.edge(person, "reads", book);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenLabelIsNull() {
        Person person = Person.builder().withName("Poliana").withAge().build();
        Book book = Book.builder().withAge(2007).withName("The Shack").build();
        graphTemplate.edge(person, null, book);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnNullWhenInboundIdIsNull() {
        Person person = Person.builder().withName("Poliana").withAge().build();
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        graphTemplate.edge(person, "reads", book);

    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnNullWhenOutboundIdIsNull() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = Book.builder().withAge(2007).withName("The Shack").build();
        graphTemplate.edge(person, "reads", book);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldReturnEntityNotFoundWhenOutBoundDidNotFound() {
        Person person = Person.builder().withId(10L).withName("Poliana").withAge().build();
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        graphTemplate.edge(person, "reads", book);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldReturnEntityNotFoundWhenInBoundDidNotFound() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = Book.builder().withId("10").withAge(2007).withName("The Shack").build();
        graphTemplate.edge(person, "reads", book);
    }

    @Test
    public void shouldCreateAnEdge() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity<Book, Person> edge = graphTemplate.edge(person, "reads", book);

        assertEquals("reads", edge.getLabel());
        assertEquals(person, edge.getOutbound());
        assertEquals(book, edge.getInbound());
        assertTrue(edge.isEmpty());
    }

}
