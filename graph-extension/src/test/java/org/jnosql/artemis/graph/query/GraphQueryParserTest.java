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
package org.jnosql.artemis.graph.query;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(WeldJUnit4Runner.class)
public class GraphQueryParserTest {

    @Inject
    private ClassRepresentations classRepresentations;

    private GraphQueryParser parser;

    private ClassRepresentation classRepresentation;

    @Inject
    private Graph graph;

    @Before
    public void setUp() {
        graph.traversal().V().toList().forEach(Vertex::remove);
        graph.traversal().E().toList().forEach(Edge::remove);


        parser = new GraphQueryParser();
        classRepresentation = classRepresentations.get(Person.class);
    }

    @After
    public void after() {
        graph.traversal().V().toList().forEach(Vertex::remove);
        graph.traversal().E().toList().forEach(Edge::remove);

    }

    @Test
    public void shouldFindByName() {
        graph.addVertex(T.label, "Person", "name", "name");

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();
        parser.parse("findByName", new Object[]{"name"}, classRepresentation, traversal);
        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals("Person", vertex.get().label());
        VertexProperty<Object> name = vertex.get().property("name");
        assertEquals("name", name.value());

    }

    @Test
    public void shouldFindByNameAndAge() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();
        parser.parse("findByNameAndAge", new Object[]{"name", 10}, classRepresentation, traversal);

        Optional<Vertex> vertex = traversal.tryNext();
        assertTrue(vertex.isPresent());
        assertEquals("Person", vertex.get().label());
        VertexProperty<Object> name = vertex.get().property("name");
        VertexProperty<Object> age = vertex.get().property("age");
        assertEquals("name", name.value());
        assertEquals(10, age.value());

    }


    @Test
    public void shouldFindByAgeLessThan() {

        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 8);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();
        parser.parse("findByAgeLessThan", new Object[]{10}, classRepresentation, traversal);
        assertEquals(2, traversal.toList().size());

    }

    @Test
    public void shouldFindByAgeGreaterThan() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 11);
        graph.addVertex(T.label, "Person", "name", "name4", "age", 12);
        graph.addVertex(T.label, "Person", "name", "name5", "age", 13);
        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.parse("findByAgeGreaterThan", new Object[]{10}, classRepresentation, traversal);
        assertEquals(3, traversal.toList().size());


    }


    @Test
    public void shouldFindByAgeLessEqualThan() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 11);
        graph.addVertex(T.label, "Person", "name", "name4", "age", 12);
        graph.addVertex(T.label, "Person", "name", "name5", "age", 13);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.parse("findByAgeLessEqualThan", new Object[]{10}, classRepresentation, traversal);
        assertEquals(2, traversal.toList().size());

    }

    @Test
    public void shouldFindByAgeGreaterEqualThan() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 11);
        graph.addVertex(T.label, "Person", "name", "name4", "age", 12);
        graph.addVertex(T.label, "Person", "name", "name5", "age", 13);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();


        parser.parse("findByAgeGreaterEqualThan", new Object[]{10}, classRepresentation, traversal);
    }


    @Test
    public void shouldFindByNameAndAAgeBetween() {
        graph.addVertex(T.label, "Person", "name", "name", "age", 10);
        graph.addVertex(T.label, "Person", "name", "name2", "age", 9);
        graph.addVertex(T.label, "Person", "name", "name3", "age", 11);
        graph.addVertex(T.label, "Person", "name", "name4", "age", 12);
        graph.addVertex(T.label, "Person", "name", "name5", "age", 13);

        GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();

        parser.parse("findByNameAndAgeBetween", new Object[]{"name", 10, 12},
                classRepresentation, traversal);

        assertEquals(1, traversal.toList().size());
    }

}