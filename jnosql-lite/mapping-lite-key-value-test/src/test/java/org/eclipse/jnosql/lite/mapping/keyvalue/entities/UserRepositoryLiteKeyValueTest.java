/*
 *   Copyright (c) 2023 Contributors to the Eclipse Foundation
 *    All rights reserved. This program and the accompanying materials
 *    are made available under the terms of the Eclipse Public License v1.0
 *    and Apache License v2.0 which accompanies this distribution.
 *    The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *    and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *    You may elect to redistribute this code under either of these licenses.
 *
 *    Contributors:
 *
 *    Otavio Santana
 */
package org.eclipse.jnosql.lite.mapping.keyvalue.entities;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import jakarta.nosql.keyvalue.KeyValueTemplate;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryLiteKeyValueTest {

    @Mock
    private KeyValueTemplate template;

    @InjectMocks
    private UserRepositoryLiteKeyValue userRepository;


    @Test
    public void shouldSaveEntity() {
        User user = new User();
        userRepository.save(user);
        verify(template, times(1)).put(eq(user));
    }

    @Test
    public void shouldSaveAllEntities() {
        User user1 = new User();
        User user2 = new User();
        Iterable<User> entities = Arrays.asList(user1, user2);

        userRepository.saveAll(entities);

        verify(template, times(1)).insert(eq(entities));
    }

    @Test
    public void shouldDeleteById() {
        String id = "123";

        userRepository.deleteById(id);

        verify(template, times(1)).deleteByKey(eq(id));
    }

    @Test
    public void shouldFindById() {
        String id = "123";
        when(template.get(eq(id), eq(User.class))).thenReturn(Optional.of(new User()));

        userRepository.findById(id);

        verify(template, times(1)).get(eq(id), eq(User.class));
    }

    @Test
    public void shouldFindAllByIds() {
        String id1 = "123";
        String id2 = "456";
        Iterable<String> ids = Arrays.asList(id1, id2);
        when(template.get(eq(id1), eq(User.class))).thenReturn(Optional.of(new User()));
        when(template.get(eq(id2), eq(User.class))).thenReturn(Optional.of(new User()));

        List<User> users = userRepository.findAllById(ids).toList();

        verify(template, times(2)).get(anyString(), eq(User.class));

        Assertions.assertThat(users).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    public void shouldCheckIfEntityExistsById() {
        String id = "123";
        when(template.get(eq(id), eq(User.class))).thenReturn(Optional.of(new User()));

        boolean exists = userRepository.existsById(id);

        assertTrue(exists);
    }

    @Test
    public void shouldReturnFalseIfEntityDoesNotExistById() {
        String id = "123";
        when(template.get(eq(id), eq(User.class))).thenReturn(Optional.empty());

        boolean exists = userRepository.existsById(id);

        assertFalse(exists);
    }

    @Test
    public void shouldThrowUnsupportedOperationExceptionOnCount() {
        assertThrows(UnsupportedOperationException.class, () -> userRepository.count());
    }

    @Test
    public void shouldThrowUnsupportedOperationExceptionOnFindAllWithPageable() {
        assertThrows(UnsupportedOperationException.class, () -> userRepository.findAll(null));
    }

    @Test
    public void shouldThrowUnsupportedOperationExceptionOnFindAll() {
        assertThrows(UnsupportedOperationException.class, () -> userRepository.findAll());
    }

    @Test
    public void shouldThrowExceptionOnFindByName() {
        String name = "John";
        assertThrows(UnsupportedOperationException.class, () -> userRepository.findByName(name));
    }
}
