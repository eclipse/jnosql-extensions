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
package org.eclipse.jnosql.mapping.arangodb.document;

import org.eclipse.jnosql.communication.document.Document;
import org.eclipse.jnosql.communication.document.DocumentEntity;
import org.eclipse.jnosql.mapping.Converters;
import org.eclipse.jnosql.mapping.document.DocumentEntityConverter;
import org.eclipse.jnosql.mapping.document.DocumentEventPersistManager;
import org.eclipse.jnosql.mapping.document.DocumentWorkflow;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;
import jakarta.nosql.tck.test.CDIExtension;
import org.eclipse.jnosql.communication.arangodb.document.ArangoDBDocumentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.when;


@CDIExtension
public class DefaultArangoDBTemplateTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private EntitiesMetadata entities;
    @Inject
    private Converters converters;

    private ArangoDBDocumentManager manager;

    private ArangoDBTemplate template;


    @BeforeEach
    public void setup() {
        manager = Mockito.mock(ArangoDBDocumentManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultArangoDBTemplate(instance, converter, flow, persistManager, entities, converters);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("_id", "Ada"));
        entity.add(Document.of("age", 10));

    }

    @Test
    public void shouldFindAQL() {
        Map<String, Object> params = Collections.singletonMap("name", "Ada");
        template.aql("FOR p IN Person FILTER p.name = @name RETURN p", params);
        Mockito.verify(manager).aql("FOR p IN Person FILTER p.name = @name RETURN p", params);
    }

    @Test
    public void shouldFindAQLWithTypeAndParameters() {
        Map<String, Object> params = Collections.singletonMap("name", "Ada");
        template.aql("FOR p IN Person FILTER p.name = @name RETURN p", params, String.class);
        Mockito.verify(manager).aql("FOR p IN Person FILTER p.name = @name RETURN p", params, String.class);
    }

    @Test
    public void shouldFindAQLWithType() {
        template.aql("FOR p IN Person FILTER p.name = @name RETURN p", String.class);
        Mockito.verify(manager).aql("FOR p IN Person FILTER p.name = @name RETURN p", String.class);
    }


}