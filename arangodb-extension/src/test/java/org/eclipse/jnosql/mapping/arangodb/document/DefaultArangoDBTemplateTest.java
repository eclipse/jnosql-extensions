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
package org.eclipse.jnosql.mapping.arangodb.document;

import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import org.eclipse.jnosql.mapping.reflection.ClassMappings;
import org.eclipse.jnosql.mapping.test.CDIExtension;
import org.eclipse.jnosql.diana.arangodb.document.ArangoDBDocumentCollectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
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
    private ClassMappings mappings;
    @Inject
    private Converters converters;

    private ArangoDBDocumentCollectionManager manager;

    private ArangoDBTemplate template;


    @BeforeEach
    public void setup() {
        manager = Mockito.mock(ArangoDBDocumentCollectionManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultArangoDBTemplate(instance, converter, flow, persistManager, mappings, converters);

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