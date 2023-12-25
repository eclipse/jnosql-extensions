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
import jakarta.data.page.Pageable;
import org.assertj.core.api.Assertions;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.graph.VertexTraversal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonCrudRepositoryLiteGraphTest {

    @Mock
    private GraphTemplate template;

    @InjectMocks
    private PersonCrudRepositoryLiteGraph repository;


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
        person.setId(1L);

        repository.delete(person);

        verify(template,times(1)).delete(eq(person.getId()));
    }

    @Test
    void shouldDeleteAllByIds() {
        repository.deleteByIdIn(List.of(1L, 2L, 3L));

        verify(template, times(3)).delete(anyLong());
    }

    @Test
    void shouldDeleteAllEntities() {
        repository.deleteAll();

        verify(template).deleteAll(eq(Person.class));
    }

    @Test
    void shouldFindById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(Optional.of(new Person()));

        repository.findById(id);

        verify(template).find(eq(Person.class), eq(id));
    }

    @Test
    void shouldCountEntities() {
        when(template.count(eq(Person.class))).thenReturn(5L);

        repository.count();

        verify(template).count(eq(Person.class));
    }

    @Test
    void shouldCheckIfEntityExistsById() {
        Long id = 123L;
        when(template.find(eq(Person.class), eq(id))).thenReturn(Optional.of(new Person()));

        repository.existsById(id);

        verify(template).find(eq(Person.class), eq(id));
    }

    @Test
    void shouldFindAllEntities() {
        repository.findAll();

        verify(template).findAll(eq(Person.class));
    }

    @Test
    void shouldFindAllEntitiesWithPageable() {
        Pageable pageable = Pageable.ofPage(1);

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

}
