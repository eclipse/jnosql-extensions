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
package org.jnosql.artemis.graph.connections.column;


import jakarta.nosql.column.Column;
import jakarta.nosql.column.ColumnEntity;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManager;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManagerAsync;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;

import static org.mockito.Mockito.when;

public class MockProducer {


    @Produces
    public CassandraColumnFamilyManager getManager() {
        CassandraColumnFamilyManager manager = Mockito.mock(CassandraColumnFamilyManager.class);
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.add(Column.of("name", "Ada"));
        when(manager.insert(Mockito.any(ColumnEntity.class))).thenReturn(entity);
        return manager;
    }

    @Produces
    public CassandraColumnFamilyManagerAsync getManagerAsync() {
        return Mockito.mock(CassandraColumnFamilyManagerAsync.class);
    }
}
