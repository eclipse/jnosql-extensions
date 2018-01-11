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

import org.hamcrest.Matchers;
import org.jnosql.artemis.EntityNotFoundException;
import org.jnosql.artemis.graph.cdi.CDIJUnitRunner;
import org.jnosql.artemis.graph.model.Book;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.diana.api.Value;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(CDIJUnitRunner.class)
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
        Person person = Person.builder().withId(-10L).withName("Poliana").withAge().build();
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        graphTemplate.edge(person, "reads", book);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldReturnEntityNotFoundWhenInBoundDidNotFound() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = Book.builder().withId(10L).withAge(2007).withName("The Shack").build();
        graphTemplate.edge(person, "reads", book);
    }

    @Test
    public void shouldCreateAnEdge() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);

        assertEquals("reads", edge.getLabel());
        assertEquals(person, edge.getOutbound());
        assertEquals(book, edge.getInbound());
        assertTrue(edge.isEmpty());
        assertNotNull(edge.getId());
    }

    @Test
    public void shouldUseAnEdge() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);

        EdgeEntity sameEdge = graphTemplate.edge(person, "reads", book);

        assertEquals(edge.getId(), sameEdge.getId());
        assertEquals(edge, sameEdge);
    }

    @Test
    public void shouldUseAnEdge2() {
        Person poliana = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Person nilzete = graphTemplate.insert(Person.builder().withName("Nilzete").withAge().build());

        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(poliana, "reads", book);
        EdgeEntity edge1 = graphTemplate.edge(nilzete, "reads", book);

        EdgeEntity sameEdge = graphTemplate.edge(poliana, "reads", book);
        EdgeEntity sameEdge1 = graphTemplate.edge(nilzete, "reads", book);

        assertEquals(edge.getId(), sameEdge.getId());
        assertEquals(edge, sameEdge);

        assertEquals(edge1.getId(), sameEdge1.getId());
        assertEquals(edge1, sameEdge1);

    }
    @Test
    public void shouldUseADifferentEdge() {
        Person poliana = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Person nilzete = graphTemplate.insert(Person.builder().withName("Nilzete").withAge().build());

        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(poliana, "reads", book);
        EdgeEntity edge1 = graphTemplate.edge(nilzete, "reads", book);

        EdgeEntity sameEdge = graphTemplate.edge(poliana, "reads", book);
        EdgeEntity sameEdge1 = graphTemplate.edge(nilzete, "reads", book);

        assertNotEquals(edge.getId(), edge1.getId());
        assertNotEquals(edge.getId(), sameEdge1.getId());

        assertNotEquals(sameEdge1.getId(), sameEdge.getId());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenAddKeyIsNull() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);
        edge.add(null, "Brazil");
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenAddValueIsNull() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);
        edge.add("where", null);
    }

    @Test
    public void shouldAddProperty() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);
        edge.add("where", "Brazil");

        assertFalse(edge.isEmpty());
        assertEquals(1, edge.size());
        assertThat(edge.getProperties(), Matchers.contains(ArtemisProperty.of("where", "Brazil")));
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenRemoveNullKeyProperty() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);
        edge.add("where", "Brazil");

        assertFalse(edge.isEmpty());
        edge.remove(null);
    }

    @Test
    public void shouldRemoveProperty() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);
        edge.add("where", "Brazil");
        assertEquals(1, edge.size());
        assertFalse(edge.isEmpty());
        edge.remove("where");
        assertTrue(edge.isEmpty());
        assertEquals(0, edge.size());
    }


    @Test
    public void shouldFindProperty() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);
        edge.add("where", "Brazil");

        Optional<Value> where = edge.get("where");
        assertTrue(where.isPresent());
        assertEquals("Brazil", where.get().get());
    }

    @Test
    public void shouldDeleteAnEdge() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);
        edge.delete();

        EdgeEntity newEdge = graphTemplate.edge(person, "reads", book);
        assertNotEquals(edge.getId(), newEdge.getId());

        graphTemplate.deleteEdge(newEdge.getId().get());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenDeleteAnEdgeWithNull() {
        graphTemplate.delete(null);
    }

    @Test
    public void shouldDeleteAnEdge2() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());

        EdgeEntity edge = graphTemplate.edge(person, "reads", book);

        graphTemplate.deleteEdge(edge.getId().get());

        EdgeEntity newEdge = graphTemplate.edge(person, "reads", book);
        assertNotEquals(edge.getId(), newEdge.getId());
    }


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenFindEdgeWithNull() {
        graphTemplate.edge(null);
    }


    @Test
    public void shouldFindAnEdge() {
        Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge().build());
        Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        EdgeEntity edge = graphTemplate.edge(person, "reads", book);

        Optional<EdgeEntity> newEdge = graphTemplate.edge(edge.getId().get());

        assertTrue(newEdge.isPresent());
        assertEquals(edge.getId(), newEdge.get().getId());

        graphTemplate.deleteEdge(edge.getId().get());
    }

    @Test
    public void shouldNotFindAnEdge() {
        Optional<EdgeEntity> edgeEntity = graphTemplate.edge(12L);

        assertFalse(edgeEntity.isPresent());
    }

}
