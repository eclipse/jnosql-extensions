/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
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


import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.assertj.core.api.Assertions;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.graph.VertexTraversal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonRepositoryLiteGraphTest {

    @Mock
    private GraphTemplate template;

    @InjectMocks
    private PersonRepositoryLiteGraph repository;


    @Test
    void shouldSaveEntity() {
        Person person = new Person();
        when(template.insert(any(Person.class))).thenReturn(person);

        repository.save(person);

        verify(template).insert(person);
    }

    @Test
    void shouldUpdateEntityOnSaveIfIdExists() {
        Person person = new Person();
        person.setId(1L);
        when(template.find( Person.class, 1L)).thenReturn(Optional.of(person));
        when(template.update(any(Person.class))).thenReturn(person);

        repository.save(person);

        verify(template).update(person);
        verify(template).find(eq(Person.class), any());
    }

    @Test
    void shouldDeleteById() {
        Long id = 123L;

        repository.deleteById(id);

        verify(template).delete(id);
    }

    @Test
    void shouldDeleteEntity() {
        Person person = new Person();
        person.setId(0L);

        repository.delete(person);

        verify(template,times(1)).delete(eq(person.getId()));
    }

    @Test
    void shouldDeleteAllByIds() {
        repository.deleteByIdIn(List.of(1L, 2L, 3L));

        verify(template, times(3)).delete(anyLong());
    }

    @Test
    void shouldFindById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(java.util.Optional.of(new Person()));

        repository.findById(id);

        verify(template).find(eq(Person.class), eq(id));
    }

    @Test
    void shouldCountEntities() {
        when(template.count(eq(Person.class))).thenReturn(5L);

        repository.countBy();

        verify(template).count(eq(Person.class));
    }

    @Test
    void shouldCheckIfEntityExistsById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(java.util.Optional.of(new Person()));

        repository.existsById(id);

        verify(template).find(eq(Person.class), eq(id));
    }

    @Test
    void shouldFindAllEntities() {
        repository.findAll();

        verify(template).findAll(eq(Person.class));
    }

    @Test
    void shouldFindAllEntitiesWithPageRequest() {
        PageRequest pageable = PageRequest.ofPage(1);

        VertexTraversal traversalVertex = Mockito.mock(VertexTraversal.class);

        when(template.traversalVertex()).thenReturn(traversalVertex);
        when(traversalVertex.hasLabel(eq(Person.class))).thenReturn(traversalVertex);
        when(traversalVertex.skip(anyLong())).thenReturn(traversalVertex);
        when(traversalVertex.limit(anyLong())).thenReturn(traversalVertex);
        when(traversalVertex.result()).thenReturn(Stream.of(new Person()));


        Page<Person> page = repository.findAll(pageable);

        verify(template).traversalVertex();
        Assertions.assertThat(page).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void shouldThrowUnsupportedExceptionForFindByName() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findByName("John");
        });
    }

    @Test
    void shouldThrowUnsupportedExceptionForQuery() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.query("John");
        });
    }

    @Test
    void shouldThrowUnsupportedExceptionForExistsByName() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.existsByName("John");
        });
    }

    @Test
    void shouldThrowUnsupportedExceptionForCountByName() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.countByName("John");
        });
    }

    @Test
    void shouldThrowUnsupportedExceptionForDeleteByName() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.deleteByName("John");
        });
    }


    @Test
    void shouldInsertPerson(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        Person result = this.repository.insertPerson(person);
        assertThat(result).isNotNull().isEqualTo(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldInsertVoid(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        this.repository.insertPersonVoid(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldInsertInt(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        var result = this.repository.insertPersonInt(person);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldInsertLong(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        var result = this.repository.insertPersonLong(person);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldInsertPersonIterable(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(people))).thenReturn(people);
        Iterable<Person> result = this.repository.insertIterable(people);
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).insert(eq(people));
    }

    @Test
    void shouldInsertPersonIterableVoid(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(people))).thenReturn(people);
        this.repository.insertIterableVoid(people);
        Mockito.verify(template).insert(eq(people));
    }

    @Test
    void shouldInsertPersonIterableInt(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(people))).thenReturn(people);
        var result = this.repository.insertIterableInt(people);
        Mockito.verify(template).insert(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldInsertPersonIterableLong(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(people))).thenReturn(people);
        var result = this.repository.insertIterableLong(people);
        Mockito.verify(template).insert(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }


    @Test
    void shouldInsertPersonArray(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.insert(eq(people))).thenReturn(people);
        Person[] result = this.repository.insertArray(new Person[]{person});
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).insert(eq(people));
    }

    @Test
    void shouldInsertPersonArrayVoid(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.insert(eq(people))).thenReturn(people);
        this.repository.insertArrayVoid(new Person[]{person});
        Mockito.verify(template).insert(eq(people));
    }

    @Test
    void shouldInsertPersonArrayInt(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.insert(eq(people))).thenReturn(people);
        var result = this.repository.insertArrayInt(new Person[]{person});
        Mockito.verify(template).insert(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldInsertPersonArrayLong(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.insert(eq(people))).thenReturn(people);
        var result = this.repository.insertArrayLong(new Person[]{person});
        Mockito.verify(template).insert(eq(people));
        Assertions.assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldUpdatePerson(){
        Person person = ada();
        when(template.update(eq(person))).thenReturn(person);
        Person result = this.repository.updatePerson(person);
        assertThat(result).isNotNull().isEqualTo(person);
        Mockito.verify(template).update(eq(person));
    }

    @Test
    void shouldUpdateVoid(){
        Person person = ada();
        when(template.update(eq(person))).thenReturn(person);
        this.repository.updatePersonVoid(person);
        Mockito.verify(template).update(eq(person));
    }

    @Test
    void shouldUpdateInt(){
        Person person = ada();
        when(template.update(eq(person))).thenReturn(person);
        int result = this.repository.updatePersonInt(person);
        Mockito.verify(template).update(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }


    @Test
    void shouldUpdateLong(){
        Person person = ada();
        when(template.update(eq(person))).thenReturn(person);
        var result = this.repository.updatePersonLong(person);
        Mockito.verify(template).update(eq(person));
        Assertions.assertThat(result).isEqualTo(1L);
    }

    @Test
    void shouldUpdatePersonIterable(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.update(eq(people))).thenReturn(people);
        Iterable<Person> result = this.repository.updateIterable(people);
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).update(eq(people));
    }

    @Test
    void shouldUpdatePersonIterableVoid(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.update(eq(people))).thenReturn(people);
        this.repository.updateIterableVoid(people);
        Mockito.verify(template).update(eq(people));
    }

    @Test
    void shouldUpdatePersonIterableInt(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.update(eq(people))).thenReturn(people);
        int result = this.repository.updateIterableInt(people);
        Mockito.verify(template).update(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldUpdatePersonIterableLong(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.update(eq(people))).thenReturn(people);
        var result = this.repository.updateIterableLong(people);
        Mockito.verify(template).update(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }


    @Test
    void shouldUpdatePersonArray(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.update(eq(people))).thenReturn(people);
        Person[] result = this.repository.updateArray(new Person[]{person});
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).update(eq(people));
    }

    @Test
    void shouldUpdatePersonArrayVoid(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.update(eq(people))).thenReturn(people);
        this.repository.updateArrayVoid(new Person[]{person});
        Mockito.verify(template).update(eq(people));
    }

    @Test
    void shouldUpdatePersonArrayInt(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.update(eq(people))).thenReturn(people);
        int result = this.repository.updateArrayInt(new Person[]{person});
        Mockito.verify(template).update(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldUpdatePersonArrayLong(){
        Person person = ada();
        List<Person> people = Collections.singletonList(person);
        when(template.update(eq(people))).thenReturn(people);
        var result = this.repository.updateArrayLong(new Person[]{person});
        Mockito.verify(template).update(eq(people));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSavePerson(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        Person result = this.repository.savePerson(person);
        assertThat(result).isNotNull().isEqualTo(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSaveVoid(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        this.repository.savePersonVoid(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSaveInt(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        var result = this.repository.savePersonInt(person);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSaveLong(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        var result = this.repository.savePersonLong(person);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }


    @Test
    void shouldSavePersonIterable(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(person))).thenReturn(person);
        Iterable<Person> result = this.repository.saveIterable(people);
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSavePersonIterableVoid(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(person))).thenReturn(person);
        this.repository.saveIterableVoid(people);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSavePersonIterableInt(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(person))).thenReturn(person);
        var result = this.repository.saveIterableInt(people);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSavePersonIterableLong(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        when(template.insert(eq(person))).thenReturn(person);
        var result = this.repository.saveIterableLong(people);
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSavePersonArray(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        Person[] result = this.repository.saveArray(new Person[]{person});
        assertThat(result).isNotNull().contains(person);
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSavePersonArrayVoid(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        this.repository.saveArrayVoid(new Person[]{person});
        Mockito.verify(template).insert(eq(person));
    }

    @Test
    void shouldSavePersonArrayInt(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        var result = this.repository.saveArrayInt(new Person[]{person});
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldSavePersonArrayLong(){
        Person person = ada();
        when(template.insert(eq(person))).thenReturn(person);
        var result = this.repository.saveArrayLong(new Person[]{person});
        Mockito.verify(template).insert(eq(person));
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldDeletePerson(){
        Person person = ada();
        var result = this.repository.deletePerson(person);
        assertThat(result).isTrue();
        Mockito.verify(template).delete(person.getId());
    }

    @Test
    void shouldDeleteVoid(){
        Person person = ada();
        this.repository.deletePersonVoid(person);
        Mockito.verify(template).delete(person.getId());
    }

    @Test
    void shouldDeleteInt(){
        Person person = ada();
        var result = this.repository.deletePersonInt(person);
        Mockito.verify(template).delete(person.getId());
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldDeleteLong(){
        Person person = ada();
        var result = this.repository.deletePersonLong(person);
        Mockito.verify(template).delete(person.getId());
        Assertions.assertThat(result).isEqualTo(1);
    }


    @Test
    void shouldDeletePersonIterable(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        var result = this.repository.deleteIterable(people);
        assertThat(result).isTrue();
        Mockito.verify(template).delete(person.getId());
    }

    @Test
    void shouldDeletePersonIterableVoid(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        this.repository.deleteIterableVoid(people);
        Mockito.verify(template).delete(person.getId());
    }

    @Test
    void shouldDeletePersonIterableInt(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        var result = this.repository.deleteIterableInt(people);
        Mockito.verify(template).delete(person.getId());
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldDeletePersonIterableLong(){
        Person person = ada();
        Set<Person> people = Collections.singleton(person);
        var result = this.repository.deleteIterableLong(people);
        Mockito.verify(template).delete(person.getId());
        Assertions.assertThat(result).isEqualTo(1);
    }


    @Test
    void shouldDeletePersonArray(){
        Person person = ada();
        var result = this.repository.deleteArray(new Person[]{person});
        assertThat(result).isTrue();
        Mockito.verify(template).delete(person.getId());
    }

    @Test
    void shouldDeletePersonArrayVoid(){
        Person person = ada();
        this.repository.deleteArrayVoid(new Person[]{person});
        Mockito.verify(template).delete(person.getId());
    }

    @Test
    void shouldDeletePersonArrayInt(){
        Person person = ada();
        var result = this.repository.deleteArrayInt(new Person[]{person});
        Mockito.verify(template).delete(person.getId());
        Assertions.assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldDeletePersonArrayLong(){
        Person person = ada();
        var result = this.repository.deleteArrayLong(new Person[]{person});
        Mockito.verify(template).delete(person.getId());
        Assertions.assertThat(result).isEqualTo(1L);
    }

    private static Person ada() {
        Person person = new Person();
        person.setId(10L);
        person.setAge(12);
        person.setName("Ada");
        return person;
    }

}
