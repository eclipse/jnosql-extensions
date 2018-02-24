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

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.graph.cdi.CDIExtension;
import org.jnosql.artemis.graph.model.Movie;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.graph.model.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(CDIExtension.class)
class DefaultGraphConverterTest {

    @Inject
    private GraphConverter converter;

    @Inject
    private Graph graph;

    @Test
    public void shouldReturnErroWhenToVertexHasNullParameter() {
        assertThrows(NullPointerException.class, () -> {
           converter.toEntity(null);
        });
    }

    @Test
    public void shouldReturnToEntity() {
        Vertex vertex = graph.addVertex(T.label, "Person", "age", 22, "name", "Ada");
        Person person = converter.toEntity(vertex);

        assertNotNull(person.getId());
        assertEquals("Ada", person.getName());
        assertEquals(Integer.valueOf(22), Integer.valueOf(person.getAge()));
    }

    @Test
    public void shouldReturnToEntityWithDifferentMap() {
        Vertex vertex = graph.addVertex(T.label, "movie", "title", "Matrix", "movie_year", "1999");
        Movie movie = converter.toEntity(vertex);

        assertEquals("Matrix", movie.getTitle());
        assertEquals(1999, movie.getYear());
    }

    @Test
    public void shouldReturnToEntityUsingConverter() {
        Vertex vertex = graph.addVertex(T.label, "Worker", "name", "James", "money", "USD 1000");
        Worker worker = converter.toEntity(vertex);

        assertEquals("James", worker.getName());
        assertEquals("USD", worker.getSalary().getCurrency());
        assertTrue(BigDecimal.valueOf(1_000).compareTo(worker.getSalary().getValue()) == 0);
    }
}