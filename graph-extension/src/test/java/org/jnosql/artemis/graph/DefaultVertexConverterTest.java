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
import org.jnosql.artemis.graph.model.Actor;
import org.jnosql.artemis.graph.model.Director;
import org.jnosql.artemis.graph.model.Job;
import org.jnosql.artemis.graph.model.Money;
import org.jnosql.artemis.graph.model.Movie;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.graph.model.Worker;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.Value;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(WeldJUnit4Runner.class)
public class DefaultVertexConverterTest {

    @Inject
    private VertexConverter converter;

    private ArtemisElement[] elements;

    private Actor actor = Actor.actorBuilder().withAge()
            .withId()
            .withName()
            .withPhones(Arrays.asList("234", "2342"))
            .withMovieCharacter(Collections.singletonMap("JavaZone", "Jedi"))
            .withMovierRating(Collections.singletonMap("JavaZone", 10))
            .build();

    @Before
    public void init() {

        elements = new ArtemisElement[]{ArtemisElement.of("_id", 12L),
                ArtemisElement.of("age", 10), ArtemisElement.of("name", "Otavio"), ArtemisElement.of("phones", Arrays.asList("234", "2342"))
                , ArtemisElement.of("movieCharacter", Collections.singletonMap("JavaZone", "Jedi"))
                , ArtemisElement.of("movieRating", Collections.singletonMap("JavaZone", 10))};
    }

    @Test
    public void shouldConvertPersonToVertex() {

        Person person = Person.builder().withAge()
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).build();

        ArtemisVertex entity = converter.toGraph(person);
        assertEquals("Person", entity.getLabel());
        assertEquals(4, entity.size());
        assertThat(entity.getProperties(), containsInAnyOrder(ArtemisElement.of("_id", 12L),
                ArtemisElement.of("age", 10), ArtemisElement.of("name", "Otavio"), ArtemisElement.of("phones", Arrays.asList("234", "2342"))));

    }

    @Test
    public void shouldConvertActorToVertex() {


        ArtemisVertex entity = converter.toGraph(actor);
        assertEquals("Actor", entity.getLabel());
        assertEquals(6, entity.size());


        assertThat(entity.getProperties(), containsInAnyOrder(elements));
    }

    @Test
    public void shouldConvertVertexToActor() {
        ArtemisVertex entity = ArtemisVertex.of("Actor");
        Stream.of(elements).forEach(entity::add);

        Actor actor = converter.toEntity(Actor.class, entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(12L, actor.getId());
        assertEquals(Arrays.asList("234", "2342"), actor.getPhones());
        assertEquals(Collections.singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(Collections.singletonMap("JavaZone", 10), actor.getMovieRating());
    }

    @Test
    public void shouldConvertVertexToActorFromEntity() {
        ArtemisVertex entity = ArtemisVertex.of("Actor");
        Stream.of(elements).forEach(entity::add);

        Actor actor = converter.toEntity(entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(12L, actor.getId());
        assertEquals(Arrays.asList("234", "2342"), actor.getPhones());
        assertEquals(Collections.singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(Collections.singletonMap("JavaZone", 10), actor.getMovieRating());
    }

    @Test
    public void shouldConvertDirectorToVertex() {

        Movie movie = new Movie("Matriz", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ArtemisVertex entity = converter.toGraph(director);
        assertEquals(5, entity.size());

        assertEquals(getValue(entity.find("name")), director.getName());
        assertEquals(getValue(entity.find("age")), director.getAge());
        assertEquals(getValue(entity.find("_id")), director.getId());
        assertEquals(getValue(entity.find("phones")), director.getPhones());

        ArtemisElement subElement = entity.find("movie").get();
        List<ArtemisElement> elements = subElement.get(new TypeReference<List<ArtemisElement>>() {
        });
        assertEquals(3, elements.size());
        assertEquals("movie", subElement.getKey());

        assertEquals(movie.getTitle(), getValue(elements.stream().filter(d -> "title".equals(d.getKey())).findFirst()));
        assertEquals(movie.getYear(), getValue(elements.stream().filter(d -> "year".equals(d.getKey())).findFirst()));
        assertEquals(movie.getActors(), getValue(elements.stream().filter(d -> "actors".equals(d.getKey())).findFirst()));


    }


    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn() {
        Movie movie = new Movie("Matrix", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ArtemisVertex entity = converter.toGraph(director);
        Director director1 = converter.toEntity(entity);

        assertEquals(movie, director1.getMovie());
        assertEquals(director.getName(), director1.getName());
        assertEquals(director.getAge(), director1.getAge());
        assertEquals(director.getId(), director1.getId());
    }


    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn2() {
        Movie movie = new Movie("Matrix", 2012, singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ArtemisVertex entity = converter.toGraph(director);
        entity.remove("movie");
        entity.add(ArtemisElement.of("title", "Matrix"));
        entity.add(ArtemisElement.of("year", 2012));
        entity.add(ArtemisElement.of("actors", singleton("Actor")));
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
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ArtemisVertex entity = converter.toGraph(director);
        entity.remove("movie");

        Map<String, Object> map = new HashMap<>();
        map.put("title", "Matrix");
        map.put("year", 2012);
        map.put("actors", singleton("Actor"));

        entity.add(ArtemisElement.of("movie", map));
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
        ArtemisVertex entity = converter.toGraph(worker);
        assertEquals("Worker", entity.getLabel());
        assertEquals("Bob", entity.find("name").get().get());
        ArtemisElement subElement = entity.find("job").get();
        List<ArtemisElement> elements = subElement.get(new TypeReference<List<ArtemisElement>>() {
        });
        assertThat(elements, Matchers.containsInAnyOrder(ArtemisElement.of("city", "Sao Paulo"), ArtemisElement.of("description", "Java Developer")));
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
        ArtemisVertex entity = converter.toGraph(worker);
        Worker worker1 = converter.toEntity(entity);
        Assert.assertEquals(worker.getSalary(), worker1.getSalary());
        assertEquals(job.getCity(), worker1.getJob().getCity());
        assertEquals(job.getDescription(), worker1.getJob().getDescription());
    }

    private Object getValue(Optional<ArtemisElement> element) {
        return element.map(ArtemisElement::getValue).map(Value::get).orElse(null);
    }

}