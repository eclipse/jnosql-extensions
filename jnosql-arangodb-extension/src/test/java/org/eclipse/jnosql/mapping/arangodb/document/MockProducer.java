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


import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import org.eclipse.jnosql.communication.arangodb.document.ArangoDBDocumentManager;
import org.mockito.Mockito;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.interceptor.Interceptor;
import java.util.function.Supplier;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
public class MockProducer implements Supplier<ArangoDBDocumentManager> {

    @Produces
    @Override
    public ArangoDBDocumentManager get() {
        ArangoDBDocumentManager manager = Mockito.mock(ArangoDBDocumentManager.class);
        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        Mockito.when(manager.insert(Mockito.any(DocumentEntity.class))).thenReturn(entity);
        return manager;
    }

}
