/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.cassandra.column;


import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.column.ColumnEntity;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManager;
import org.jnosql.diana.cassandra.column.CassandraColumnFamilyManagerAsync;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;

public class MockProducer {


    @Produces
    public CassandraColumnFamilyManager getManager() {
        CassandraColumnFamilyManager manager = Mockito.mock(CassandraColumnFamilyManager.class);
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.add(Column.of("name", "Ada"));
        Mockito.when(manager.save(Mockito.any(ColumnEntity.class))).thenReturn(entity);
        return manager;
    }

    @Produces
    public CassandraColumnFamilyManagerAsync getManagerAsync() {
        return Mockito.mock(CassandraColumnFamilyManagerAsync.class);
    }
}
