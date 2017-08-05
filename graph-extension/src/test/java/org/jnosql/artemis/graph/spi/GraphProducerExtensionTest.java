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
import org.jnosql.artemis.column.ColumnTemplate;
import org.jnosql.artemis.graph.GraphTemplate;
import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.jnosql.artemis.graph.model.Person;
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


    @Test
    public void shouldInstance() {
        assertNotNull(manager);
        assertNotNull(managerMock);
    }

    @Test
    public void shouldSave() {
        Person personMock = managerMock.insert(Person.builder().build());

        assertEquals("columnRepositoryMock", personMock.getName());
    }

}