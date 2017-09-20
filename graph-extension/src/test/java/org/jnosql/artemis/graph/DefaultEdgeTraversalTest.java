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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(WeldJUnit4Runner.class)
public class DefaultEdgeTraversalTest extends AbstractTraversalTest {


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenEdgeIdIsNull() {
        graphTemplate.getTraversalEdge(null);
    }


    @Test
    public void shouldReturnEdgeId() {
        Optional<EdgeEntity<Object, Object>> edgeEntity = graphTemplate.getTraversalEdge(reads.getId().get())
                .next();

        assertTrue(edgeEntity.isPresent());
        assertEquals(reads.getId().get(), edgeEntity.get().getId().get());
    }

    @Test
    public void shouldReturnOutE() {
        List<EdgeEntity<Person, Book>> edges = graphTemplate.getTraversalVertex().outE(READS)
                .<Person, Book>stream()
                .collect(toList());

        assertEquals(3, edges.size());
        assertThat(edges, containsInAnyOrder(reads, reads2, reads3));
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorOutEWhenIsNull() {
        graphTemplate.getTraversalVertex().outE(null)
                .<Person, Book>stream()
                .collect(toList());


    }

    @Test
    public void shouldReturnInE() {
        List<EdgeEntity<Person, Book>> edges = graphTemplate.getTraversalVertex().inE(READS)
                .<Person, Book>stream()
                .collect(toList());

        assertEquals(3, edges.size());
        assertThat(edges, containsInAnyOrder(reads, reads2, reads3));
    }


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenInEIsNull() {
        graphTemplate.getTraversalVertex().inE(null)
                .<Person, Book>stream()
                .collect(toList());

    }

    @Test
    public void shouldReturnBothE() {
        List<EdgeEntity<Person, Book>> edges = graphTemplate.getTraversalVertex().bothE(READS)
                .<Person, Book>stream()
                .collect(toList());

        assertEquals(6, edges.size());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturErrorWhennBothEIsNull() {
        graphTemplate.getTraversalVertex().bothE(null)
                .<Person, Book>stream()
                .collect(toList());
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


    @Test
    public void shouldHasProperty() {
        Optional<EdgeEntity<Person, Book>> edgeEntity = graphTemplate.getTraversalVertex()
                .outE(READS)
                .has("motivation", "hobby").next();

        assertTrue(edgeEntity.isPresent());
        assertEquals(reads.getId().get(), edgeEntity.get().getId().get());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenHasPropertyWhenKeyIsNull() {
        graphTemplate.getTraversalVertex()
                .outE(READS)
                .has((String) null, "hobby").next();
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenHasPropertyWhenValueIsNull() {
        graphTemplate.getTraversalVertex()
                .outE(READS)
                .has("motivation", null).next();
    }

    @Test
    public void shouldHasNot() {
        List<EdgeEntity<Person, Book>> edgeEntities = graphTemplate.getTraversalVertex()
                .outE(READS).hasNot("language")
                .<Person, Book>stream()
                .collect(toList());

        assertEquals(2, edgeEntities.size());
    }

    @Test
    public void shouldCount() {
        long count = graphTemplate.getTraversalVertex().outE(READS).count();
        assertEquals(3L, count);
    }

    @Test
    public void shouldReturnZeroWhenCountIsEmpty() {
        long count = graphTemplate.getTraversalVertex().outE("WRITES").count();
        assertEquals(0L, count);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenHasNotIsNull() {
        graphTemplate.getTraversalVertex().outE(READS).hasNot(null);
    }


    @Test
    public void shouldMapValuesAsStream() {
        List<Map<String, Object>> maps = graphTemplate.getTraversalVertex().inE("reads")
                .valueMap("motivation").stream().collect(toList());

        assertFalse(maps.isEmpty());
        assertEquals(3, maps.size());

        List<String> names = new ArrayList<>();

        maps.forEach(m -> names.add(m.get("motivation").toString()));

        assertThat(names, containsInAnyOrder("hobby", "love", "job"));
    }

    @Test
    public void shouldMapValuesAsStreamLimit() {
        List<Map<String, Object>> maps = graphTemplate.getTraversalVertex().inE("reads")
                .valueMap("motivation").next(2).collect(toList());

        assertFalse(maps.isEmpty());
        assertEquals(2, maps.size());
    }


    @Test
    public void shouldReturnMapValueAsEmptyStream() {
        Stream<Map<String, Object>> stream = graphTemplate.getTraversalVertex().inE("reads")
                .valueMap("noFoundProperty").stream();
        assertTrue(stream.allMatch(Map::isEmpty));
    }

    @Test
    public void shouldReturnNext() {
        Map<String, Object> map = graphTemplate.getTraversalVertex().inE("reads")
                .valueMap("motivation").next();

        assertNotNull(map);
        assertFalse(map.isEmpty());
    }
}