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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.graph.cdi.CDIExtension;
import org.jnosql.artemis.graph.model.Money;
import org.jnosql.artemis.graph.model.Movie;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.graph.model.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CDIExtension.class)
class DefaultGraphConverterTest {

    @Inject
    private GraphConverter converter;

    @Inject
    private GraphTraversalSource graph;

    @BeforeEach
    public void setUp() {
        graph.V().toList().forEach(Vertex::remove);
        graph.E().toList().forEach(Edge::remove);
    }

    @Test
    public void shouldReturnErrorWhenToEntityHasNullParameter() {
        assertThrows(NullPointerException.class, () -> converter.toEntity(null));
    }

    @Test
    public void shouldReturnToEntity() {
        Vertex vertex = graph.addV("Person").property( "age", 22).property( "name", "Ada").next();
        //Vertex vertex = graph.addVertex(T.label, "Person", "age", 22, "name", "Ada");
        Person person = converter.toEntity(vertex);

        assertNotNull(person.getId());
        assertEquals("Ada", person.getName());
        assertEquals(Integer.valueOf(22), Integer.valueOf(person.getAge()));
    }

    @Test
    public void shouldReturnToEntityInstance() {
        Vertex vertex = graph.addV("Person").property( "age", 22).property( "name", "Ada").next();
        //Vertex vertex = graph.addVertex(T.label, "Person", "age", 22, "name", "Ada");
        Person person = Person.builder().build();
        Person result = converter.toEntity(person, vertex);

        assertTrue(person == result);
        assertNotNull(person.getId());
        assertEquals("Ada", person.getName());
        assertEquals(Integer.valueOf(22), Integer.valueOf(person.getAge()));
    }

    @Test
    public void shouldReturnToEntityWithDifferentMap() {
        Vertex vertex = graph.addV("movie").property( "title", "Matrix").property( "movie_year", "1999").next();
        //Vertex vertex = graph.addVertex(T.label, "movie", "title", "Matrix", "movie_year", "1999");
        Movie movie = converter.toEntity(vertex);

        assertEquals("Matrix", movie.getTitle());
        assertEquals(1999, movie.getYear());
    }

    @Test
    public void shouldReturnToEntityUsingConverter() {
        Vertex vertex = graph.addV("Worker").property( "name", "James").property( "money", "USD 1000").next();
        //Vertex vertex = graph.addVertex(T.label, "Worker", "name", "James", "money", "USD 1000");
        Worker worker = converter.toEntity(vertex);

        assertEquals("James", worker.getName());
        assertEquals("USD", worker.getSalary().getCurrency());
        assertTrue(BigDecimal.valueOf(1_000).compareTo(worker.getSalary().getValue()) == 0);
    }

    @Test
    public void shouldReturnErrorWhenToVertexHasNullParameter() {
        assertThrows(NullPointerException.class, () -> converter.toVertex(null));
    }


    @Test
    public void shouldConvertEntityToTinkerPopVertex() {
        Person person = Person.builder().withName("Ada").withAge(22).build();
        Vertex vertex = converter.toVertex(person);

        assertEquals("Person", vertex.label());
        assertEquals("Ada", vertex.value("name"));
        assertEquals(Integer.valueOf(22), vertex.value("age"));
    }

    @Test
    public void shouldConvertEntityToTinkerPopVertexUsingNativeName() {
        Movie movie = new Movie("Matrix", 1999, null);
        Vertex vertex = converter.toVertex(movie);

        assertEquals("movie", vertex.label());
        assertEquals(1999, Number.class.cast(vertex.value("movie_year")).intValue());
        assertEquals("Matrix", vertex.value("title"));
    }


    @Test
    public void shouldConvertEntityToTinkerPopVertexUsingConverter() {
        Worker worker = new Worker();
        worker.setName("Alexandre");
        worker.setSalary(new Money("BRL", BigDecimal.valueOf(1_000L)));

        Vertex vertex = converter.toVertex(worker);
        assertEquals("Worker", vertex.label());
        assertEquals("BRL 1000", vertex.value("money"));
        assertEquals("Alexandre", vertex.value("name"));
    }

    @Test
    public void shouldConvertEntityWithIdExistToTinkerPopVertex() {
        Vertex adaVertex = graph.addV("Person").property( "age", 22).property( "name", "Ada").next();
        //Vertex adaVertex = graph.addVertex(T.label, "Person", "age", 22, "name", "Ada");
        Person person = Person.builder().withName("Ada").withAge(22).withId((Long) adaVertex.id()).build();
        Vertex vertex = converter.toVertex(person);

        assertEquals(vertex.id(), adaVertex.id());
        assertEquals("Person", vertex.label());
        assertEquals("Ada", vertex.value("name"));
        assertEquals(Integer.valueOf(22), vertex.value("age"));
    }

    @Test
    public void shouldConvertEntityWithIdDoesNotExistToTinkerPopVertex() {

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            Person person = Person.builder().withName("Ada").withAge(22).withId(10L).build();
            Vertex vertex = converter.toVertex(person);
        });

        assertEquals("Vertex does not support user supplied identifiers", exception.getMessage());

    }

    @Test
    public void shouldReturnErrorWhenToEdgeEntityIsNull() {
        assertThrows(NullPointerException.class, () -> converter.toEdgeEntity(null));
    }


    @Test
    public void shouldToEdgeEntity() {
        Vertex matrixVertex = graph.addV("movie").property( "title", "Matrix").property( "movie_year", "1999").next();
        Vertex adaVertex = graph.addV("Person").property( "age", 22).property( "name", "Ada").next();        
        //Vertex matrixVertex = graph.addVertex(T.label, "movie", "title", "Matrix", "movie_year", "1999");
        //Vertex adaVertex = graph.addVertex(T.label, "Person", "age", 22, "name", "Ada");
        Edge edge = adaVertex.addEdge("watch", matrixVertex);
        edge.property("feel", "like");

        EdgeEntity edgeEntity = converter.toEdgeEntity(edge);
        Person ada = edgeEntity.getOutgoing();
        Movie matrix = edgeEntity.getIncoming();

        assertNotNull(edgeEntity);
        assertEquals("watch", edgeEntity.getLabel());
        assertNotNull(edgeEntity.getId());
        assertEquals(edge.id(), edgeEntity.getId().get());

        assertEquals("Ada", ada.getName());
        assertEquals(22, ada.getAge());

        assertEquals("Matrix", matrix.getTitle());
        assertEquals(1999L, matrix.getYear());
    }

    @Test
    public void shouldReturnToEdgeErrorWhenIsNull() {
        assertThrows(NullPointerException.class, () -> converter.toEdge(null));
    }

    @Test
    public void shouldReturnToEdge() {
        Vertex matrixVertex = graph.addV("movie").property( "title", "Matrix").property( "movie_year", "1999").next();
        Vertex adaVertex = graph.addV("Person").property( "age", 22).property( "name", "Ada").next();        
        //Vertex matrixVertex = graph.addVertex(T.label, "movie", "title", "Matrix", "movie_year", "1999");
        //Vertex adaVertex = graph.addVertex(T.label, "Person", "age", 22, "name", "Ada");
        Edge edge = adaVertex.addEdge("watch", matrixVertex);

        EdgeEntity edgeEntity = converter.toEdgeEntity(edge);
        Edge edge1 = converter.toEdge(edgeEntity);

        assertEquals(edge.id(), edge1.id());
    }
}