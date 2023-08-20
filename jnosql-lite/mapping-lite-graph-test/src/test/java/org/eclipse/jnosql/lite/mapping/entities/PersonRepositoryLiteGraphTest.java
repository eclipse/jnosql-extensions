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


import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
    public void shouldSaveEntity() {
        Person person = new Person();
        when(template.insert(any(Person.class))).thenReturn(person);

        repository.save(person);

        verify(template).insert(person);
    }

    @Test
    public void shouldUpdateEntityOnSaveIfIdExists() {
        Person person = new Person();
        person.setId(1L);
        when(template.find( Person.class, 1L)).thenReturn(Optional.of(person));
        when(template.update(any(Person.class))).thenReturn(person);

        repository.save(person);

        verify(template).update(person);
        verify(template).find(eq(Person.class), any());
    }

    @Test
    public void shouldDeleteById() {
        Long id = 123L;

        repository.deleteById(id);

        verify(template).deleteById(id);
    }

    @Test
    public void shouldDeleteEntity() {
        Person person = new Person();

        repository.delete(person);

        verify(template).delete(person);
    }

    @Test
    public void shouldDeleteAllByIds() {
        repository.deleteAllById(List.of(1L, 2L, 3L));

        verify(template, times(3)).deleteById(anyLong());
    }

    @Test
    public void shouldDeleteAllEntities() {
        repository.deleteAll();

        verify(template).delete(eq(Person.class));
    }

    @Test
    public void shouldFindById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(java.util.Optional.of(new Person()));

        repository.findById(id);

        verify(template).find(eq(Person.class), eq(id));
    }

    @Test
    public void shouldCountEntities() {
        when(template.count(eq(Person.class))).thenReturn(5L);

        repository.count();

        verify(template).count(eq(Person.class));
    }

    @Test
    public void shouldCheckIfEntityExistsById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(java.util.Optional.of(new Person()));

        repository.existsById(id);

        verify(template).find(eq(Person.class), eq(id));
    }

    @Test
    public void shouldFindAllEntities() {
        repository.findAll();

        verify(template).findAll(eq(Person.class));
    }

    @Test
    public void shouldFindAllEntitiesWithPageable() {
        Pageable pageable = Pageable.ofPage(1);

        when(template.traversalVertex()
                .hasLabel(eq("Person"))
                .skip(anyInt())
                .limit(anyInt())
                .result()
                .toList())
                .thenReturn(List.of(new Person()));

        Page<Person> page = repository.findAll(pageable);

        verify(template).traversalVertex();
        // Add assertions for the Page object
    }

    @Test
    public void shouldThrowUnsupportedExceptionForFindByName() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findByName("John");
        });
    }

    @Test
    public void shouldThrowUnsupportedExceptionForQuery() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.query("John");
        });
    }

    @Test
    public void shouldThrowUnsupportedExceptionForExistsByName() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.existsByName("John");
        });
    }

    @Test
    public void shouldThrowUnsupportedExceptionForCountByName() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.countByName("John");
        });
    }

    @Test
    public void shouldThrowUnsupportedExceptionForDeleteByName() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.deleteByName("John");
        });
    }

}
