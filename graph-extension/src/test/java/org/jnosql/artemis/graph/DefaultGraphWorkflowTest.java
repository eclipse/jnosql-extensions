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

import org.jnosql.artemis.graph.cdi.CDIExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(CDIExtension.class)
public class DefaultGraphWorkflowTest {


    @InjectMocks
    private DefaultGraphWorkflow subject;

    @Mock
    private GraphEventPersistManager graphEventPersistManager;

    @Mock
    private VertexConverter converter;

    @Mock
    private ArtemisVertex artemisVertex;


    @BeforeEach
    public void setUp() {
        when(converter.toVertex(any(Object.class)))
                .thenReturn(artemisVertex);

    }

    @Test
    public void shouldReturnErrorWhenEntityIsNull() {
        assertThrows(NullPointerException.class, () -> {
            UnaryOperator<ArtemisVertex> action = t -> t;
            subject.flow(null, action);
        });
    }

    @Test
    public void shouldReturnErrorWhenActionIsNull() {
        assertThrows(NullPointerException.class, () -> {
            subject.flow("", null);
        });
    }

    @Test
    public void shouldFollowWorkflow() {
        UnaryOperator<ArtemisVertex> action = t -> t;
        subject.flow("entity", action);

        verify(graphEventPersistManager).firePreGraph(any(ArtemisVertex.class));
        verify(graphEventPersistManager).firePostGraph(any(ArtemisVertex.class));
        verify(graphEventPersistManager).firePreEntity(any(ArtemisVertex.class));
        verify(graphEventPersistManager).firePostEntity(any(ArtemisVertex.class));

        verify(graphEventPersistManager).firePreGraphEntity(any(ArtemisVertex.class));
        verify(graphEventPersistManager).firePostGraphEntity(any(ArtemisVertex.class));
        verify(converter).toVertex(any(Object.class));
    }

}