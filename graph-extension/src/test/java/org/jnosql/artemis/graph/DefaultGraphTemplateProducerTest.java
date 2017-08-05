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
package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(WeldJUnit4Runner.class)
public class DefaultGraphTemplateProducerTest {

    @Inject
    private GraphTemplateProducer producer;

    @Test(expected = NullPointerException.class)
    public void shouldReturnErrorWhenManagerNull() {
        producer.get(null);
    }

    @Test
    public void shouldReturn() {
        Graph manager = Mockito.mock(Graph.class);
        GraphTemplate template = producer.get(manager);
        assertNotNull(template);
    }
}