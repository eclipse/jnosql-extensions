/*
 *  Copyright (c) 2023 Otávio Santana and others
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
package org.eclipse.jnosql.lite.mapping;

import jakarta.data.repository.DataRepository;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class LiteClassScannerTest {

    @Test
    public void testEntitiesIsEmpty() {
        LiteClassScanner scanner = new LiteClassScanner();
        Set<Class<?>> entities = scanner.entities();

        assertTrue(entities.isEmpty());
    }

    @Test
    public void testRepositoriesIsEmpty() {
        LiteClassScanner scanner = new LiteClassScanner();
        Set<Class<?>> repositories = scanner.repositories();

        assertTrue(repositories.isEmpty());
    }

    @Test
    public void testEmbeddablesIsEmpty() {
        LiteClassScanner scanner = new LiteClassScanner();
        Set<Class<?>> embeddables = scanner.embeddables();

        assertTrue(embeddables.isEmpty());
    }

    @Test
    public void testRepositoriesWithFilterIsEmpty() {
        LiteClassScanner scanner = new LiteClassScanner();
        Set<Class<?>> repositoriesWithFilter = scanner.repositories(SomeDataRepository.class);

        assertTrue(repositoriesWithFilter.isEmpty());
    }

    @Test
    public void testRepositoriesStandardIsEmpty() {
        LiteClassScanner scanner = new LiteClassScanner();
        Set<Class<?>> repositoriesStandard = scanner.repositoriesStandard();

        assertTrue(repositoriesStandard.isEmpty());
    }

    interface SomeDataRepository<ID, E> extends DataRepository<ID, E> {
        // Define a simple data repository class for testing purposes
    }


}

