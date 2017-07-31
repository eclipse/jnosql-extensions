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

import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.jnosql.artemis.graph.model.Book;
import org.jnosql.artemis.graph.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(WeldJUnit4Runner.class)
public class DefaultEdgeTraversalTest extends AbstractTraversalTest {


    @Test
    public void shouldReturnOutE() {
        List<EdgeEntity<Person, Book>> edges = graphTemplate.getTraversalVertex().outE(READS)
                .<Person, Book>stream()
                .collect(toList());

        assertEquals(3, edges.size());
        assertThat(edges, containsInAnyOrder(reads, reads2, reads3));
    }

    @Test
    public void shouldReturnInE() {
        List<EdgeEntity<Person, Book>> edges = graphTemplate.getTraversalVertex().inE(READS)
                .<Person, Book>stream()
                .collect(toList());

        assertEquals(3, edges.size());
        assertThat(edges, containsInAnyOrder(reads, reads2, reads3));
    }

    @Test
    public void shouldReturnBothE() {
        List<EdgeEntity<Person, Book>> edges = graphTemplate.getTraversalVertex().bothE(READS)
                .<Person, Book>stream()
                .collect(toList());

        assertEquals(6, edges.size());
    }


    @Test
    public void shouldReturnOut() {
        List<Person> people = graphTemplate.getTraversalVertex().outE(READS).outV().<Person>stream().collect(toList());
        assertEquals(3, people.size());
        assertThat(people, containsInAnyOrder(poliana, otavio, paulo));
    }

    @Test
    public void shouldReturnIn() {
        List<Book> books = graphTemplate.getTraversalVertex().outE(READS).inV().<Book>stream().collect(toList());
        assertEquals(3, books.size());
        assertThat(books, containsInAnyOrder(shack, effectiveJava, license));
    }


    @Test
    public void shouldReturnBoth() {
        List<?> entities = graphTemplate.getTraversalVertex().outE(READS).bothV().stream().collect(toList());
        assertEquals(6, entities.size());
        assertThat(entities, containsInAnyOrder(shack, effectiveJava, license, paulo, otavio, poliana));
    }
}