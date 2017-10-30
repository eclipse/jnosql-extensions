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

import org.jnosql.artemis.graph.cdi.CDIJUnitRunner;
import org.jnosql.artemis.graph.model.Actor;
import org.jnosql.artemis.graph.model.Director;
import org.jnosql.artemis.graph.model.Job;
import org.jnosql.artemis.graph.model.Money;
import org.jnosql.artemis.graph.model.Movie;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.graph.model.Worker;
import org.jnosql.diana.api.Value;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(CDIJUnitRunner.class)
public class DefaultVertexConverterTest {

    @Inject
    private VertexConverter converter;

    private ArtemisProperty[] elements;

    private Actor actor = Actor.actorBuilder().withAge()
            .withId()
            .withName()
            .withPhones(asList("234", "2342"))
            .withMovieCharacter(singletonMap("JavaZone", "Jedi"))
            .withMovierRating(singletonMap("JavaZone", 10))
            .build();

    @Before
    public void init() {

        elements = new ArtemisProperty[]{
                ArtemisProperty.of("age", 10),
                ArtemisProperty.of("name", "Otavio"),
                ArtemisProperty.of("phones", asList("234", "2342"))
                , ArtemisProperty.of("movieCharacter", singletonMap("JavaZone", "Jedi"))
                , ArtemisProperty.of("movieRating", singletonMap("JavaZone", 10))};
    }

    @Test
    public void shouldConvertPersonToVertex() {

        Person person = Person.builder().withAge()
                .withId(12L)
                .withName("Otavio")
                .withPhones(asList("234", "2342")).build();

        ArtemisVertex entity = converter.toVertex(person);
        assertEquals("Person", entity.getLabel());
        assertEquals(3, entity.size());
        assertThat(entity.getProperties(), containsInAnyOrder(
                ArtemisProperty.of("age", 10), ArtemisProperty.of("name", "Otavio"), ArtemisProperty.of("phones", asList("234", "2342"))));

    }

    @Test
    public void shouldConvertActorToVertex() {


        ArtemisVertex entity = converter.toVertex(actor);
        assertEquals("Actor", entity.getLabel());
        assertEquals(5, entity.size());
        assertEquals(12L, entity.getId().get().get());

        assertThat(entity.getProperties(), containsInAnyOrder(elements));
    }

    @Test
    public void shouldConvertVertexToActor() {
        ArtemisVertex entity = ArtemisVertex.of("Actor", 12L);
        Stream.of(elements).forEach(entity::add);

        Actor actor = converter.toEntity(Actor.class, entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(Long.valueOf(12L), actor.getId());
        assertEquals(asList("234", "2342"), actor.getPhones());
        assertEquals(singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(singletonMap("JavaZone", 10), actor.getMovieRating());
    }

    @Test
    public void shouldConvertVertexToActorFromEntity() {
        ArtemisVertex entity = ArtemisVertex.of("Actor", 12L);
        Stream.of(elements).forEach(entity::add);

        Actor actor = converter.toEntity(entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(Long.valueOf(12L), actor.getId());
        assertEquals(asList("234", "2342"), actor.getPhones());
        assertEquals(singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(singletonMap("JavaZone", 10), actor.getMovieRating());
    }

    @Test
    public void shouldConvertDirectorToVertex() {

        Movie movie = new Movie("Matriz", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(asList("234", "2342")).withMovie(movie).build();

        ArtemisVertex entity = converter.toVertex(director);
        assertEquals(6, entity.size());

        assertEquals(getValue(entity.find("name")), director.getName());
        assertEquals(getValue(entity.find("age")), director.getAge());
        assertEquals(entity.getId().get().get(Long.class), director.getId());
        assertEquals(getValue(entity.find("phones")), director.getPhones());

        assertEquals(movie.getTitle(), getValue(entity.find("title")));
        assertEquals(movie.getYear(), getValue(entity.find("movie_year")));
        assertEquals(movie.getActors(), getValue(entity.find("actors")));

    }


    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn() {
        Movie movie = new Movie("Matrix", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(asList("234", "2342")).withMovie(movie).build();

        ArtemisVertex entity = converter.toVertex(director);
        Director directorConverted = converter.toEntity(entity);

        assertEquals(movie, directorConverted.getMovie());
        assertEquals(director.getName(), directorConverted.getName());
        assertEquals(director.getAge(), directorConverted.getAge());
        assertEquals(director.getId(), directorConverted.getId());
    }


    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn2() {
        Movie movie = new Movie("Matrix", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(asList("234", "2342")).withMovie(movie).build();

        ArtemisVertex entity = converter.toVertex(director);
        entity.remove("movie");
        entity.add(ArtemisProperty.of("title", "Matrix"));
        entity.add(ArtemisProperty.of("year", 2012));
        entity.add(ArtemisProperty.of("actors", singleton("Actor")));
        Director director1 = converter.toEntity(entity);

        assertEquals(movie, director1.getMovie());
        assertEquals(director.getName(), director1.getName());
        assertEquals(director.getAge(), director1.getAge());
        assertEquals(director.getId(), director1.getId());
    }


    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn3() {
        Movie movie = new Movie("Matrix", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(asList("234", "2342")).withMovie(movie).build();

        ArtemisVertex entity = converter.toVertex(director);
        entity.remove("movie");

        entity.add("title", "Matrix");
        entity.add("movie_year", 2012);
        entity.add("actors", singleton("Actor"));

        Director director1 = converter.toEntity(entity);

        assertEquals(movie, director1.getMovie());
        assertEquals(director.getName(), director1.getName());
        assertEquals(director.getAge(), director1.getAge());
        assertEquals(director.getId(), director1.getId());
    }

    @Test
    public void shouldConvertToVertexWhenHaConverter() {
        Worker worker = new Worker();
        Job job = new Job();
        job.setCity("Sao Paulo");
        job.setDescription("Java Developer");
        worker.setName("Bob");
        worker.setSalary(new Money("BRL", BigDecimal.TEN));
        worker.setJob(job);
        ArtemisVertex entity = converter.toVertex(worker);
        assertEquals("Worker", entity.getLabel());
        assertEquals("Bob", entity.find("name").get().get());

        assertEquals("Sao Paulo", entity.find("city").get().get());
        assertEquals("Java Developer", entity.find("description").get().get());

        assertEquals("BRL 10", entity.find("money").get().get());
    }

    @Test
    public void shouldConvertToEntityWhenHasConverter() {
        Worker worker = new Worker();
        Job job = new Job();
        job.setCity("Sao Paulo");
        job.setDescription("Java Developer");
        worker.setName("Bob");
        worker.setSalary(new Money("BRL", BigDecimal.TEN));
        worker.setJob(job);
        ArtemisVertex entity = converter.toVertex(worker);
        Worker worker1 = converter.toEntity(entity);
        Assert.assertEquals(worker.getSalary(), worker1.getSalary());
        assertEquals(job.getCity(), worker1.getJob().getCity());
        assertEquals(job.getDescription(), worker1.getJob().getDescription());
    }

    private Object getValue(Optional<ArtemisProperty> element) {
        return element.map(ArtemisProperty::getValue).map(Value::get).orElse(null);
    }

}