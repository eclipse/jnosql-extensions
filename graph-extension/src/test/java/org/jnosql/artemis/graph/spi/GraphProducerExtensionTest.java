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
package org.jnosql.artemis.graph.spi;

import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.artemis.graph.BookRepository;
import org.jnosql.artemis.graph.GraphTemplate;
import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.jnosql.artemis.graph.model.Book;
import org.jnosql.artemis.graph.model.Person;
import org.jnosql.artemis.key.KeyValueTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(WeldJUnit4Runner.class)
public class GraphProducerExtensionTest {

    @Inject
    @Database(value = DatabaseType.GRAPH, provider = "graphRepositoryMock")
    private GraphTemplate managerMock;

    @Inject
    private GraphTemplate manager;

    @Inject
    @Database(value = DatabaseType.GRAPH, provider = "graphRepositoryMock")
    private KeyValueTemplate repositoryMock;

    @Inject
    private BookRepository repository;


    @Test
    public void shouldInstance() {
        assertNotNull(manager);
        assertNotNull(managerMock);
    }

    @Test
    public void shouldSave() {
        Person personMock = managerMock.insert(Person.builder().build());

        assertEquals("nameMock", personMock.getName());
    }

    @Test
    public void shouldGet() {
        Book user = repository.findById("user").get();
        Book userDefault = repository.findById("user").get();
        Book userMock = repository.findById("user").get();
        assertEquals("Default", user.getName());
        assertEquals("Default", userDefault.getName());
        assertEquals("keyvalueMock", userMock.getName());
    }

}