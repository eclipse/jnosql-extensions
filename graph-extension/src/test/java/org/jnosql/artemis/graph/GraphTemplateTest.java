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

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.EntityNotFoundException;
import org.jnosql.artemis.IdNotFoundException;
import org.jnosql.artemis.graph.cdi.CDIJUnitRunner;
import org.jnosql.artemis.graph.model.Animal;
import org.jnosql.artemis.graph.model.Book;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.graph.model.WrongEntity;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.jnosql.artemis.graph.model.Person.builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(CDIJUnitRunner.class)
public class GraphTemplateTest  {

    @Inject
    private GraphTemplate graphTemplate;

    @Inject
    private Graph graph;

    @After
    public void after() {
        graph.traversal().V().toList().forEach(Vertex::remove);
        graph.traversal().E().toList().forEach(Edge::remove);
    }


    @Test(expected = IdNotFoundException.class)
    public void shouldReturnErrorWhenThereIsNotId() {
        WrongEntity entity = new WrongEntity("lion");
        graphTemplate.insert(entity);
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

        graphTemplate.delete(updated.getId());
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

        graphTemplate.delete(update.getId());
    }


    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorInFindWhenIdIsNull() {
        graphTemplate.find(null);
    }

    @Test
    public void shouldFindAnEntity() {
        Person person = builder().withAge()
                .withName("Otavio").build();
        Person updated = graphTemplate.insert(person);
        Optional<Person> personFound = graphTemplate.find(updated.getId());

        assertTrue(personFound.isPresent());
        assertEquals(updated, personFound.get());

        graphTemplate.delete(updated.getId());
    }

    @Test
    public void shouldNotFindAnEntity() {
        Optional<Person> personFound = graphTemplate.find(0L);
        assertFalse(personFound.isPresent());
    }

    @Test
    public void shouldDeleteAnEntity() {

        Person person = graphTemplate.insert(builder().withAge()
                .withName("Otavio").build());

        assertTrue(graphTemplate.find(person.getId()).isPresent());
        graphTemplate.delete(person.getId());
        assertFalse(graphTemplate.find(person.getId()).isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenGetEdgesIdHasNullId() {
        graphTemplate.getEdgesById(null, Direction.BOTH);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenGetEdgesIdHasNullDirection() {
        graphTemplate.getEdgesById(10, null);
    }

    @Test
    public void shouldReturnEmptyWhenVertexDoesNotExist() {
        Collection<EdgeEntity> edges = graphTemplate.getEdgesById(10, Direction.BOTH);
        assertTrue(edges.isEmpty());
    }

    @Test
    public void shouldReturnEdgesById() {
        Person otavio = graphTemplate.insert(builder().withAge()
                .withName("Otavio").build());

        Animal dog = graphTemplate.insert(new Animal("dog"));
        Book cleanCode = graphTemplate.insert(Book.builder().withName("Clean code").build());

        EdgeEntity likes = graphTemplate.edge(otavio, "likes", dog);
        EdgeEntity reads = graphTemplate.edge(otavio, "reads", cleanCode);

        Collection<EdgeEntity> edgesById = graphTemplate.getEdgesById(otavio.getId(), Direction.BOTH);
        Collection<EdgeEntity> edgesById1 = graphTemplate.getEdgesById(otavio.getId(), Direction.BOTH, "reads");
        Collection<EdgeEntity> edgesById2 = graphTemplate.getEdgesById(otavio.getId(), Direction.BOTH, () -> "likes");
        Collection<EdgeEntity> edgesById3 = graphTemplate.getEdgesById(otavio.getId(), Direction.OUT);
        Collection<EdgeEntity> edgesById4 = graphTemplate.getEdgesById(cleanCode.getId(), Direction.IN);

        assertEquals(edgesById, edgesById3);
        assertThat(edgesById, containsInAnyOrder(likes, reads));
        assertThat(edgesById1, containsInAnyOrder(reads));
        assertThat(edgesById2, containsInAnyOrder(likes));
        assertThat(edgesById4, containsInAnyOrder(reads));



    }


}