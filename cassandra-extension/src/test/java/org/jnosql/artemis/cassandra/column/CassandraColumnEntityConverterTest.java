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
package org.jnosql.artemis.cassandra.column;

import org.jnosql.artemis.cassandra.column.model.Actor;
import org.jnosql.artemis.cassandra.column.model.Artist;
import org.jnosql.artemis.cassandra.column.model.Director;
import org.jnosql.artemis.cassandra.column.model.History;
import org.jnosql.artemis.cassandra.column.model.History2;
import org.jnosql.artemis.cassandra.column.model.Job;
import org.jnosql.artemis.cassandra.column.model.Money;
import org.jnosql.artemis.cassandra.column.model.Movie;
import org.jnosql.artemis.cassandra.column.model.Worker;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.cassandra.column.UDT;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(WeldJUnit4Runner.class)
public class CassandraColumnEntityConverterTest {


    @Inject
    private CassandraColumnEntityConverter converter;

    @Inject
    private ClassRepresentations classRepresentations;

    private Column[] columns;

    private Actor actor = Actor.actorBuilder().withAge()
            .withId()
            .withName()
            .withPhones(Arrays.asList("234", "2342"))
            .withMovieCharacter(Collections.singletonMap("JavaZone", "Jedi"))
            .withMovierRating(Collections.singletonMap("JavaZone", 10))
            .build();

    @Before
    public void init() {

        columns = new Column[]{Column.of("_id", 12L),
                Column.of("age", 10), Column.of("name", "Otavio"),
                Column.of("phones", Arrays.asList("234", "2342"))
                , Column.of("movieCharacter", Collections.singletonMap("JavaZone", "Jedi"))
                , Column.of("movieRating", Collections.singletonMap("JavaZone", 10))};
    }

    @Test
    public void shouldConvertPersonToDocument() {

        Artist artist = Artist.builder().withAge()
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).build();

        ColumnEntity entity = converter.toColumn(artist);
        assertEquals("Artist", entity.getName());
        assertEquals(4, entity.size());
        /*Assert.assertThat(entity.getColumns(), containsInAnyOrder(Document.of("_id", 12L),
                Document.of("age", 10), Document.of("name", "Otavio"), Document.of("phones", Arrays.asList("234", "2342"))));*/

    }

    @Test
    public void shouldConvertActorToDocument() {


        ColumnEntity entity = converter.toColumn(actor);
        assertEquals("Actor", entity.getName());
        assertEquals(6, entity.size());


        assertThat(entity.getColumns(), containsInAnyOrder(columns));
    }

    @Test
    public void shouldConvertDocumentToActor() {
        ColumnEntity entity = ColumnEntity.of("Actor");
        Stream.of(columns).forEach(entity::add);

        Actor actor = converter.toEntity(Actor.class, entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(12L, actor.getId());
        assertEquals(Arrays.asList("234", "2342"), actor.getPhones());
        assertEquals(Collections.singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(Collections.singletonMap("JavaZone", 10), actor.getMovieRating());
    }

    @Test
    public void shouldConvertDocumentToActorFromEntity() {
        ColumnEntity entity = ColumnEntity.of("Actor");
        Stream.of(columns).forEach(entity::add);

        Actor actor = converter.toEntity(entity);
        assertNotNull(actor);
        assertEquals(10, actor.getAge());
        assertEquals(12L, actor.getId());
        assertEquals(Arrays.asList("234", "2342"), actor.getPhones());
        assertEquals(Collections.singletonMap("JavaZone", "Jedi"), actor.getMovieCharacter());
        assertEquals(Collections.singletonMap("JavaZone", 10), actor.getMovieRating());
    }


    @Test
    public void shouldConvertDirectorToColumn() {

        Movie movie = new Movie("Matrix", 2012, Collections.singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ColumnEntity entity = converter.toColumn(director);
        assertEquals(5, entity.size());

        assertEquals(getValue(entity.find("name")), director.getName());
        assertEquals(getValue(entity.find("age")), director.getAge());
        assertEquals(getValue(entity.find("_id")), director.getId());
        assertEquals(getValue(entity.find("phones")), director.getPhones());


        Column subColumn = entity.find("movie").get();
        List<Column> columns = subColumn.get(new TypeReference<List<Column>>() {
        });

        assertEquals(3, columns.size());
        assertEquals("movie", subColumn.getName());
        assertEquals(movie.getTitle(), columns.stream().filter(c -> "title".equals(c.getName())).findFirst().get().get());
        assertEquals(movie.getYear(), columns.stream().filter(c -> "year".equals(c.getName())).findFirst().get().get());
        assertEquals(movie.getActors(), columns.stream().filter(c -> "actors".equals(c.getName())).findFirst().get().get());


    }

    @Test
    public void shouldConvertToEmbeddedClassWhenHasSubColumn() {
        Movie movie = new Movie("Matrix", 2012, Collections.singleton("Actor"));
        Director director = Director.builderDiretor().withAge(12)
                .withId(12)
                .withName("Otavio")
                .withPhones(Arrays.asList("234", "2342")).withMovie(movie).build();

        ColumnEntity entity = converter.toColumn(director);
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

        ColumnEntity entity = converter.toColumn(director);
        entity.remove("movie");
        entity.add(Column.of("title", "Matrix"));
        entity.add(Column.of("year", 2012));
        entity.add(Column.of("actors", singleton("Actor")));
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

        ColumnEntity entity = converter.toColumn(director);
        entity.remove("movie");
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Matrix");
        map.put("year", 2012);
        map.put("actors", singleton("Actor"));

        entity.add(Column.of("movie", map));
        Director director1 = converter.toEntity(entity);

        assertEquals(movie, director1.getMovie());
        assertEquals(director.getName(), director1.getName());
        assertEquals(director.getAge(), director1.getAge());
        assertEquals(director.getId(), director1.getId());
    }


    @Test
    public void shouldConvertToDocumentWhenHaConverter() {
        Worker worker = new Worker();
        Job job = new Job();
        job.setCity("Sao Paulo");
        job.setDescription("Java Developer");
        worker.setName("Bob");
        worker.setSalary(new Money("BRL", BigDecimal.TEN));
        worker.setJob(job);
        ColumnEntity entity = converter.toColumn(worker);
        assertEquals("Worker", entity.getName());
        assertEquals("Bob", entity.find("name").get().get());
        Column subDocument = entity.find("job").get();
        List<Column> documents = subDocument.get(new TypeReference<List<Column>>() {
        });
        assertThat(documents, containsInAnyOrder(Column.of("city", "Sao Paulo"), Column.of("description", "Java Developer")));
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
        ColumnEntity entity = converter.toColumn(worker);
        Worker worker1 = converter.toEntity(entity);
        Assert.assertEquals(worker.getSalary(), worker1.getSalary());
        assertEquals(job.getCity(), worker1.getJob().getCity());
        assertEquals(job.getDescription(), worker1.getJob().getDescription());
    }

    @Test
    @Ignore
    public void shouldSupportLocalDateConverter() {
        History history = new History();
        history.setCalendar(Calendar.getInstance());
        history.setLocalDate(LocalDate.now());
        history.setLocalDateTime(LocalDateTime.now());
        history.setZonedDateTime(ZonedDateTime.now());
        history.setNumber(new java.util.Date().getTime());

        ColumnEntity entity = converter.toColumn(history);
        assertEquals("History", entity.getName());

        Set<Object> collect = entity.getColumns().stream()
                .map(Column::get).collect(Collectors.toSet());
        assertThat(collect,
                containsInAnyOrder(com.datastax.driver.core.LocalDate
                        .fromMillisSinceEpoch(new java.util.Date().getTime())));

        History historyConverted = converter.toEntity(entity);
        assertNotNull(historyConverted);


    }

    @Test
    public void shouldSupportUDT() {
        Address address = new Address();
        address.setCity("California");
        address.setStreet("Street");

        Person person = new Person();
        person.setAge(10);
        person.setName("Ada");
        person.setHome(address);

        ColumnEntity entity = converter.toColumn(person);
        assertEquals("Person", entity.getName());
        Column column = entity.find("home").get();
        org.jnosql.diana.cassandra.column.UDT udt = org.jnosql.diana.cassandra.column.UDT.class.cast(column);

        assertEquals("address", udt.getUserType());
        assertEquals("home", udt.getName());
        assertThat((List<Column>) udt.get(),
                containsInAnyOrder(Column.of("city", "California"), Column.of("street", "Street")));

    }


    @Test
    public void shouldSupportUDTToEntity() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.add(Column.of("name", "Poliana"));
        entity.add(Column.of("age", 20));
        List<Column> columns = Arrays.asList(Column.of("city", "Salvador"), Column.of("street", "Jose Anasoh"));
        UDT udt = UDT.builder("address").withName("home")
                .addUDT(columns).build();
        entity.add(udt);

        Person person = converter.toEntity(entity);
        assertNotNull(person);
        Address home = person.getHome();
        assertEquals("Poliana", person.getName());
        assertEquals(Integer.valueOf(20), person.getAge());
        assertEquals("Salvador", home.getCity());
        assertEquals("Jose Anasoh", home.getStreet());

    }

    @Test
    public void shouldSupportTimeStampConverter() {
        History2 history = new History2();
        history.setCalendar(Calendar.getInstance());
        history.setLocalDate(LocalDate.now());
        history.setLocalDateTime(LocalDateTime.now());
        history.setZonedDateTime(ZonedDateTime.now());
        history.setNumber(new java.util.Date().getTime());

        ColumnEntity entity = converter.toColumn(history);
        assertEquals("History2", entity.getName());
        History2 historyConverted = converter.toEntity(entity);
        assertNotNull(historyConverted);

    }


    private Object getValue(Optional<Column> document) {
        return document.map(Column::getValue).map(Value::get).orElse(null);
    }
}