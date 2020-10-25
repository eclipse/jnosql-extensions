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
package org.eclipse.jnosql.mapping.orientdb.document;


import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import org.eclipse.jnosql.diana.orientdb.document.OrientDBDocumentCollectionManager;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;

public class MockProducer {

    @Produces
    public OrientDBDocumentCollectionManager getManager() {
        OrientDBDocumentCollectionManager manager = Mockito.mock(OrientDBDocumentCollectionManager.class);
        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        Mockito.when(manager.update(Mockito.any(DocumentEntity.class))).thenReturn(entity);
        return manager;
    }
}
