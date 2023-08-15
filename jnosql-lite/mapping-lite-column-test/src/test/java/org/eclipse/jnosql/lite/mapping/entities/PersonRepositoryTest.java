/*
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.lite.mapping.entities;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.nosql.PreparedStatement;
import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.communication.Condition;
import org.eclipse.jnosql.communication.column.ColumnCondition;
import org.eclipse.jnosql.communication.column.ColumnDeleteQuery;
import org.eclipse.jnosql.communication.column.ColumnQuery;
import org.eclipse.jnosql.mapping.column.JNoSQLColumnTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PersonRepositoryTest {
    @Mock
    private JNoSQLColumnTemplate template;

    @InjectMocks
    private PersonRepositoryLiteColumn personRepository;


    @Test
    public void shouldSaveEntity() {
        Person person = new Person();
        when(template.insert(eq(person))).thenReturn(person);

        Person savedPerson = personRepository.save(person);

        assertNotNull(savedPerson);
        verify(template, times(1)).insert(eq(person));
    }

    @Test
    public void shouldDeleteEntityById() {
        Long id = 123L;

        personRepository.deleteById(id);

        verify(template, times(1)).delete(eq(Person.class), eq(id));
    }

    @Test
    public void shouldFindEntityById() {
        Long id = 123L;
        Person person = new Person();
        when(template.find(eq(Person.class), eq(id))).thenReturn(Optional.of(person));

        Optional<Person> foundPerson = personRepository.findById(id);

        assertTrue(foundPerson.isPresent());
        verify(template, times(1)).find(eq(Person.class), eq(id));
    }

    @Test
    public void shouldFindAllEntities() {
        Stream<Object> personStream = Stream.of(new Person());
        when(template.select(any(ColumnQuery.class))).thenReturn(personStream);

        Stream<Person> allPersons = personRepository.findAll();

        assertNotNull(allPersons);
        verify(template, times(1)).select(any(ColumnQuery.class));
    }

    @Test
    public void shouldSaveAllEntities() {
        List<Person> persons = Arrays.asList(new Person(), new Person());
        Iterable<Person> savedPersons = personRepository.saveAll(persons);
        assertNotNull(savedPersons);
        verify(template, Mockito.times(2)).insert(new Person());
    }

    @Test
    public void shouldDeleteEntity() {
        Person person = new Person();

        personRepository.delete(person);

        verify(template, times(1)).delete(eq(person));
    }

    @Test
    public void shouldDeleteAllEntities() {
        personRepository.deleteAll();

        verify(template, times(1)).delete(eq(Person.class));
    }

    @Test
    public void shouldFindAllEntitiesByIds() {
        List<Long> ids = Arrays.asList(123L, 456L);
        Person person1 = new Person();
        Person person2 = new Person();
        when(template.find(eq(Person.class), anyLong())).thenReturn(Optional.of(person1), Optional.of(person2));

        Stream<Person> foundPersons = personRepository.findAllById(ids);

        assertNotNull(foundPersons);
        assertEquals(2, foundPersons.count());
        verify(template, times(ids.size())).find(eq(Person.class), anyLong());
    }

    @Test
    public void shouldCountEntities() {
        long expectedCount = 5L;
        when(template.count(eq(Person.class))).thenReturn(expectedCount);

        long count = personRepository.count();

        assertEquals(expectedCount, count);
        verify(template, times(1)).count(eq(Person.class));
    }

    @Test
    public void shouldCheckIfEntityExistsById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(Optional.of(new Person()));

        boolean exists = personRepository.existsById(id);

        assertTrue(exists);
        verify(template, times(1)).find(eq(Person.class), eq(id));
    }

    @Test
    public void shouldReturnFalseIfEntityDoesNotExistById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(Optional.empty());

        boolean exists = personRepository.existsById(id);

        assertFalse(exists);
        verify(template, times(1)).find(eq(Person.class), eq(id));
    }


    @Test
    public void shouldFindAllEntitiesWithPageable() {
        Pageable pageable = mock(Pageable.class);
        when(template.select(any(ColumnQuery.class))).thenReturn( Stream.of(new Person(), new Person()));

        Page<Person> page = personRepository.findAll(pageable);

        assertNotNull(page);
        assertEquals(List.of(new Person(), new Person()), page.content());
        verify(template, times(1)).select(any(ColumnQuery.class));
    }

    @Test
    public void shouldThrowExceptionIfPageableIsNull() {
        assertThrows(NullPointerException.class, () -> personRepository.findAll(null));
    }

    @Test
    public void shouldFindByName(){
        when(template.select(any(ColumnQuery.class))).thenReturn( Stream.of(new Person(), new Person()));
        List<Person> result = this.personRepository.findByName("Ada");
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        assertThat(result).isNotEmpty().hasSize(2);
        verify(template).select(captor.capture());
        ColumnQuery query = captor.getValue();
        ColumnCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.column().get(String.class)).isEqualTo("Ada");
        });

    }

    @Test
    public void shouldQuery(){
        when(template.prepare(anyString())).thenReturn(Mockito.mock(PreparedStatement.class));
        this.personRepository.query("Ada");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(template).prepare(captor.capture());
        String value = captor.getValue();
        assertThat(value).isEqualTo("select * from Person where name = @name");
    }

    @Test
    public void shouldExistByName(){
        when(template.select(any(ColumnQuery.class))).thenReturn( Stream.of(new Person(), new Person()));
        boolean result = this.personRepository.existsByName("Ada");
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        assertThat(result).isTrue();
        verify(template).select(captor.capture());
        ColumnQuery query = captor.getValue();
        ColumnCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.column().get(String.class)).isEqualTo("Ada");
        });

    }

    @Test
    public void shouldCountByName(){
        when(template.select(any(ColumnQuery.class))).thenReturn( Stream.of(new Person(), new Person()));
        long result = this.personRepository.countByName("Ada");
        ArgumentCaptor<ColumnQuery> captor = ArgumentCaptor.forClass(ColumnQuery.class);
        assertThat(result).isEqualTo(2L);
        verify(template).select(captor.capture());
        ColumnQuery query = captor.getValue();
        ColumnCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.column().get(String.class)).isEqualTo("Ada");
        });

    }

    @Test
    public void shouldDeleteByName(){
        this.personRepository.deleteByName("Ada");
        ArgumentCaptor<ColumnDeleteQuery> captor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        verify(template).delete(captor.capture());
        ColumnDeleteQuery query = captor.getValue();
        ColumnCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.column().get(String.class)).isEqualTo("Ada");
        });

    }

}