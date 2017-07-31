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

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.structure.T;
import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.jnosql.artemis.graph.model.Book;
import org.jnosql.artemis.graph.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(WeldJUnit4Runner.class)
public class DefaultVertexTraversalTest extends AbstractTraversalTest {


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenVertexIdIsNull() {
        graphTemplate.getTraversal(null);
    }


    @Test
    public void shouldGetVertexFromId() {
        List<Person> people = graphTemplate.getTraversal(otavio.getId(), poliana.getId()).<Person>stream()
                .collect(toList());

        assertThat(people, containsInAnyOrder(otavio, poliana));
    }

    @Test
    public void shouldDefineLimit() {
        List<Person> people = graphTemplate.getTraversal(otavio.getId(), poliana.getId(), paulo.getId()).limit(1).<Person>stream()
                .collect(toList());

        assertEquals(1, people.size());
        assertThat(people, containsInAnyOrder(otavio));
    }

    @Test
    public void shouldDefineLimit2() {
        List<Person> people = graphTemplate.getTraversal(otavio.getId(), poliana.getId(), paulo.getId()).
                <Person>stream(2)
                .collect(toList());

        assertEquals(2, people.size());
        assertThat(people, containsInAnyOrder(otavio, poliana));
    }

    @Test
    public void shouldNext() {
        Optional<?> next = graphTemplate.getTraversal().next();
        assertTrue(next.isPresent());
    }

    @Test
    public void shouldEmptyNext() {
        Optional<?> next = graphTemplate.getTraversal(-12).next();
        assertFalse(next.isPresent());
    }


    @Test
    public void shouldHas() {
        Optional<Person> person = graphTemplate.getTraversal().has("name", "Poliana").next();
        assertTrue(person.isPresent());
        assertEquals(person.get(), poliana);
    }

    @Test
    public void shouldHasId() {
        Optional<Person> person = graphTemplate.getTraversal().has(T.id, poliana.getId()).next();
        assertTrue(person.isPresent());
        assertEquals(person.get(), poliana);
    }

    @Test
    public void shouldHasPredicate() {
        List<?> result = graphTemplate.getTraversal().has("age", P.gt(26))
                .stream()
                .collect(toList());
        assertEquals(5, result.size());
    }

    @Test
    public void shouldHasLabel() {
        List<Book> books = graphTemplate.getTraversal().hasLabel("Book").<Book>stream().collect(toList());
        assertEquals(3, books.size());
        assertThat(books, containsInAnyOrder(shack, license, effectiveJava));
    }

    @Test
    public void shouldIn() {
        List<Book> books = graphTemplate.getTraversal().out(READS).<Book>stream().collect(toList());
        assertEquals(3, books.size());
        assertThat(books, containsInAnyOrder(shack, license, effectiveJava));
    }

    @Test
    public void shouldOut() {
        List<Person> books = graphTemplate.getTraversal().in(READS).<Person>stream().collect(toList());
        assertEquals(3, books.size());
        assertThat(books, containsInAnyOrder(otavio, poliana, paulo));
    }

    @Test
    public void shouldNot() {
        List<?> result = graphTemplate.getTraversal().hasNot("year").stream().collect(toList());
        assertEquals(6, result.size());
    }
}