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

import jakarta.data.Limit;
import jakarta.data.page.Page;
import jakarta.data.page.Pageable;
import jakarta.data.Sort;

import jakarta.nosql.PreparedStatement;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.communication.Condition;
import org.eclipse.jnosql.communication.document.DocumentCondition;
import org.eclipse.jnosql.communication.document.DocumentDeleteQuery;
import org.eclipse.jnosql.communication.document.DocumentQuery;
import org.eclipse.jnosql.lite.mapping.entities.Person;
import org.eclipse.jnosql.lite.mapping.entities.PersonRepositoryLiteDocument;
import org.eclipse.jnosql.mapping.document.JNoSQLDocumentTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PersonRepositoryTest {
    @Mock
    private JNoSQLDocumentTemplate template;

    @InjectMocks
    private PersonRepositoryLiteDocument personRepository;


    @Test
    void shouldSaveEntity() {
        Person person = new Person();
        when(template.insert(eq(person))).thenReturn(person);

        Person savedPerson = personRepository.save(person);

        assertNotNull(savedPerson);
        verify(template, times(1)).insert(eq(person));
    }

    @Test
    void shouldDeleteEntityById() {
        Long id = 123L;

        personRepository.deleteById(id);

        verify(template, times(1)).delete(eq(Person.class), eq(id));
    }

    @Test
    void shouldFindEntityById() {
        Long id = 123L;
        Person person = new Person();
        when(template.find(eq(Person.class), eq(id))).thenReturn(Optional.of(person));

        Optional<Person> foundPerson = personRepository.findById(id);

        assertTrue(foundPerson.isPresent());
        verify(template, times(1)).find(eq(Person.class), eq(id));
    }

    @Test
    void shouldFindAllEntities() {
        Stream<Object> personStream = Stream.of(new Person());
        when(template.select(any(DocumentQuery.class))).thenReturn(personStream);

        Stream<Person> allPersons = personRepository.findAll();

        assertNotNull(allPersons);
        verify(template, times(1)).select(any(DocumentQuery.class));
    }

    @Test
    void shouldSaveAllEntities() {
        List<Person> persons = Arrays.asList(new Person(), new Person());
        Iterable<Person> savedPersons = personRepository.saveAll(persons);
        assertNotNull(savedPersons);
        verify(template, Mockito.times(2)).insert(new Person());
    }

    @Test
    void shouldDeleteEntity() {
        Person person = new Person();

        personRepository.delete(person);

        verify(template,times(1)).delete(eq(Person.class),eq(person.getId()));
    }

    @Test
    void shouldDeleteAllEntities() {
        personRepository.deleteAll();

        verify(template, times(1)).deleteAll(eq(Person.class));
    }

    @Test
    void shouldUpdate(){
        this.personRepository.update(new Person());
        verify(template).update(any(Person.class));
    }

    @Test
    void shouldInsert(){
        this.personRepository.insert(new Person());
        verify(template).insert(any(Person.class));
    }

    @Test
    void shouldUpdateIterable(){
        this.personRepository.updateAll(List.of(new Person()));
        verify(template).update(any(List.class));
    }

    @Test
    void shouldInsertIterable(){
        this.personRepository.insertAll(List.of(new Person()));
        verify(template).insert(any(List.class));
    }


    @Test
    void shouldFindAllEntitiesByIds() {
        List<Long> ids = Arrays.asList(123L, 456L);
        Person person1 = new Person();
        Person person2 = new Person();
        when(template.find(eq(Person.class), anyLong())).thenReturn(Optional.of(person1), Optional.of(person2));

        Stream<Person> foundPersons = personRepository.findByIdIn(ids);

        assertNotNull(foundPersons);
        assertEquals(2, foundPersons.count());
        verify(template, times(ids.size())).find(eq(Person.class), anyLong());
    }

    @Test
    void shouldCountEntities() {
        long expectedCount = 5L;
        when(template.count(eq(Person.class))).thenReturn(expectedCount);

        long count = personRepository.count();

        assertEquals(expectedCount, count);
        verify(template, times(1)).count(eq(Person.class));
    }

    @Test
    void shouldCheckIfEntityExistsById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(Optional.of(new Person()));

        boolean exists = personRepository.existsById(id);

        assertTrue(exists);
        verify(template, times(1)).find(eq(Person.class), eq(id));
    }

    @Test
    void shouldReturnFalseIfEntityDoesNotExistById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(Optional.empty());

        boolean exists = personRepository.existsById(id);

        assertFalse(exists);
        verify(template, times(1)).find(eq(Person.class), eq(id));
    }


    @Test
    void shouldFindAllEntitiesWithPageable() {
        Pageable pageable = mock(Pageable.class);
        when(template.select(any(DocumentQuery.class))).thenReturn( Stream.of(new Person(), new Person()));

        Page<Person> page = personRepository.findAll(pageable);

        assertNotNull(page);
        assertEquals(List.of(new Person(), new Person()), page.content());
        verify(template, times(1)).select(any(DocumentQuery.class));
    }

    @Test
    void shouldThrowExceptionIfPageableIsNull() {
        assertThrows(NullPointerException.class, () -> personRepository.findAll(null));
    }



    @Test
    void shouldFindByName(){
        when(template.select(any(DocumentQuery.class))).thenReturn( Stream.of(new Person(), new Person()));
        List<Person> result = this.personRepository.findByName("Ada");
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        assertThat(result).isNotEmpty().hasSize(2);
        verify(template).select(captor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.document().get(String.class)).isEqualTo("Ada");
        });

    }

    @Test
    void shouldQuery(){
        when(template.prepare(anyString())).thenReturn(Mockito.mock(PreparedStatement.class));
        this.personRepository.query("Ada");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(template).prepare(captor.capture());
        String value = captor.getValue();
        assertThat(value).isEqualTo("select * from Person where name = @name");
    }

    @Test
    void shouldExistByName(){
        when(template.select(any(DocumentQuery.class))).thenReturn( Stream.of(new Person(), new Person()));
        boolean result = this.personRepository.existsByName("Ada");
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        assertThat(result).isTrue();
        verify(template).select(captor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.document().get(String.class)).isEqualTo("Ada");
        });

    }

    @Test
    void shouldCountByName(){
        when(template.select(any(DocumentQuery.class))).thenReturn( Stream.of(new Person(), new Person()));
        long result = this.personRepository.countByName("Ada");
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        assertThat(result).isEqualTo(2L);
        verify(template).select(captor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.document().get(String.class)).isEqualTo("Ada");
        });

    }

    @Test
    void shouldDeleteByName(){
        this.personRepository.deleteByName("Ada");
        ArgumentCaptor<DocumentDeleteQuery> captor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        verify(template).delete(captor.capture());
        DocumentDeleteQuery query = captor.getValue();
        DocumentCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.document().get(String.class)).isEqualTo("Ada");
        });

    }

    @Test
    void shouldFindPageable(){
        when(template.select(any(DocumentQuery.class))).thenReturn( Stream.of(new Person(), new Person()));
        Pageable pageable = Pageable.ofPage(10).sortBy(Sort.asc("name"));
        Page<Person> result = this.personRepository.findByName("Ada", pageable);
        ArgumentCaptor<DocumentQuery> captor = ArgumentCaptor.forClass(DocumentQuery.class);
        assertThat(result).isNotEmpty().hasSize(2);
        verify(template).select(captor.capture());
        DocumentQuery query = captor.getValue();
        DocumentCondition condition = query.condition().orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(condition.condition()).isEqualTo(Condition.EQUALS);
            soft.assertThat(condition.document().get(String.class)).isEqualTo("Ada");
            soft.assertThat(query.sorts()).hasSize(1).contains(Sort.asc("name"));
            soft.assertThat(query.skip()).isEqualTo(90L);
            soft.assertThat(query.limit()).isEqualTo(10L);
        });
    }

    @Test
    void shouldInsertPerson(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        Person result = this.personRepository.insertPerson(person);
        assertThat(result).isNotNull().isEqualTo(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldInsertVoid(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        this.personRepository.insertPersonVoid(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldInsertInt(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        int result = this.personRepository.insertPersonInt(person);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldInsertPersonIterable(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(people))).thenReturn(people);
        Iterable<Person> result = this.personRepository.insertIterable(people);
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).insert(eq(people));
    }

    @Test
    void shouldInsertPersonIterableVoid(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(people))).thenReturn(people);
        this.personRepository.insertIterableVoid(people);
        Mockito.verify(template).insert(eq(people));
    }

    @Test
    void shouldInsertPersonIterableInt(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(people))).thenReturn(people);
        int result = this.personRepository.insertIterableInt(people);
        Mockito.verify(template).insert(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldInsertPersonArray(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.insert(eq(people))).thenReturn(people);
        Person[] result = this.personRepository.insertArray(new Person[]{person});
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).insert(eq(people));
    }

    @Test
    void shouldInsertPersonArrayVoid(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.insert(eq(people))).thenReturn(people);
        this.personRepository.insertArrayVoid(new Person[]{person});
        Mockito.verify(template).insert(eq(people));
    }

    @Test
    void shouldInsertPersonArrayInt(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.insert(eq(people))).thenReturn(people);
        int result = this.personRepository.insertArrayInt(new Person[]{person});
        Mockito.verify(template).insert(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldUpdatePerson(){
        Person person = ada();
        when(template.update(eq(person))).thenReturn(person);
        Person result = this.personRepository.updatePerson(person);
        assertThat(result).isNotNull().isEqualTo(person);
        Mockito.verify(template).update(eq(person));
    }

    @Test
    void shouldUpdateVoid(){
        Person person = ada();
        when(template.update(eq(person))).thenReturn(person);
        this.personRepository.updatePersonVoid(person);
        Mockito.verify(template).update(eq(person));
    }

    @Test
    void shouldUpdateInt(){
        Person person = ada();
        when(template.update(eq(person))).thenReturn(person);
        int result = this.personRepository.updatePersonInt(person);
        Mockito.verify(template).update(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldUpdatePersonIterable(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.update(eq(people))).thenReturn(people);
        Iterable<Person> result = this.personRepository.updateIterable(people);
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).update(eq(people));
    }

    @Test
    void shouldUpdatePersonIterableVoid(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.update(eq(people))).thenReturn(people);
        this.personRepository.updateIterableVoid(people);
        Mockito.verify(template).update(eq(people));
    }

    @Test
    void shouldUpdatePersonIterableInt(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.update(eq(people))).thenReturn(people);
        int result = this.personRepository.updateIterableInt(people);
        Mockito.verify(template).update(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldUpdatePersonArray(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.update(eq(people))).thenReturn(people);
        Person[] result = this.personRepository.updateArray(new Person[]{person});
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).update(eq(people));
    }

    @Test
    void shouldUpdatePersonArrayVoid(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.update(eq(people))).thenReturn(people);
        this.personRepository.updateArrayVoid(new Person[]{person});
        Mockito.verify(template).update(eq(people));
    }

    @Test
    void shouldUpdatePersonArrayInt(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.update(eq(people))).thenReturn(people);
        int result = this.personRepository.updateArrayInt(new Person[]{person});
        Mockito.verify(template).update(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSavePerson(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        Person result = this.personRepository.savePerson(person);
        assertThat(result).isNotNull().isEqualTo(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSaveVoid(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        this.personRepository.savePersonVoid(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSaveInt(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        int result = this.personRepository.savePersonInt(person);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSavePersonIterable(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(person))).thenReturn(person);
        Iterable<Person> result = this.personRepository.saveIterable(people);
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSavePersonIterableVoid(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(person))).thenReturn(person);
        this.personRepository.saveIterableVoid(people);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSavePersonIterableInt(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(person))).thenReturn(person);
        int result = this.personRepository.saveIterableInt(people);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSavePersonArray(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        Person[] result = this.personRepository.saveArray(new Person[]{person});
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSavePersonArrayVoid(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        this.personRepository.saveArrayVoid(new Person[]{person});
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSavePersonArrayInt(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        int result = this.personRepository.saveArrayInt(new Person[]{person});
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldDeletePerson(){
        Person person = ada();
        var result = this.personRepository.deletePerson(person);
        assertThat(result).isTrue();
        Mockito.verify(template).delete(Person.class, person.getId());
    }

    @Test
    void shouldDeleteVoid(){
        Person person = ada();
        this.personRepository.deletePersonVoid(person);
        Mockito.verify(template).delete(Person.class, person.getId());
    }

    @Test
    void shouldDeleteInt(){
        Person person = ada();
        int result = this.personRepository.deletePersonInt(person);
        Mockito.verify(template).delete(Person.class, person.getId());
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldDeletePersonIterable(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        var result = this.personRepository.deleteIterable(people);
        assertThat(result).isTrue();
        Mockito.verify(template).delete(Person.class, person.getId());
    }

    @Test
    void shouldDeletePersonIterableVoid(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        this.personRepository.deleteIterableVoid(people);
        Mockito.verify(template).delete(Person.class, person.getId());
    }

    @Test
    void shouldDeletePersonIterableInt(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        int result = this.personRepository.deleteIterableInt(people);
        Mockito.verify(template).delete(Person.class, person.getId());
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldDeletePersonArray(){
        Person person = ada();
        var result = this.personRepository.deleteArray(new Person[]{person});
        assertThat(result).isTrue();
        Mockito.verify(template).delete(Person.class, person.getId());
    }

    @Test
    void shouldDeletePersonArrayVoid(){
        Person person = ada();
        this.personRepository.deleteArrayVoid(new Person[]{person});
        Mockito.verify(template).delete(Person.class, person.getId());
    }

    @Test
    void shouldDeletePersonArrayInt(){
        Person person = ada();
        int result = this.personRepository.deleteArrayInt(new Person[]{person});
        Mockito.verify(template).delete(Person.class, person.getId());
        Assertions.assertThat(result).isEqualTo(1);
    }

    private static Person ada() {
        Person person = new Person();
        person.setId(10L);
        person.setAge(12);
        person.setName("Ada");
        return person;
    }
}