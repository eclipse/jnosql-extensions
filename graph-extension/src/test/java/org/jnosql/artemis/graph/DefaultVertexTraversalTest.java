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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jnosql.artemis.graph.model.Person.builder;
import static org.junit.Assert.*;

@RunWith(WeldJUnit4Runner.class)
public class DefaultVertexTraversalTest {

    private static final String READS = "reads";
    @Inject
    private GraphTemplate graphTemplate;


    private Person otavio;
    private Person poliana;
    private Person paulo;

    private Book shack;
    private Book license;
    private Book effectiveJava;

    private EdgeEntity<Person, Book> reads;
    private EdgeEntity<Person, Book> read1;
    private EdgeEntity<Person, Book> reads2;

    @Before
    public void setUp() {

        otavio = graphTemplate.insert(builder().withAge(27)
                .withName("Otavio").build());
        poliana = graphTemplate.insert(builder().withAge(26)
                .withName("Poliana").build());
        paulo = graphTemplate.insert(builder().withAge(50)
                .withName("Paulo").build());

        shack = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        license = graphTemplate.insert(Book.builder().withAge(2013).withName("Software License").build());
        effectiveJava = graphTemplate.insert(Book.builder().withAge(2001).withName("Effective Java").build());


        reads = graphTemplate.edge(otavio, READS, effectiveJava);
        read1 = graphTemplate.edge(poliana, READS, shack);
        reads2 = graphTemplate.edge(paulo, READS, license);
    }

    @After
    public void after() {
        graphTemplate.delete(otavio.getId());
        graphTemplate.delete(poliana.getId());
        graphTemplate.delete(paulo.getId());

        graphTemplate.delete(shack.getId());
        graphTemplate.delete(license.getId());
        graphTemplate.delete(effectiveJava.getId());

        reads.delete();
        read1.delete();
        reads2.delete();
    }


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