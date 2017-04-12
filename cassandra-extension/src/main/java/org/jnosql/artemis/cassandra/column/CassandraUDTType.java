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

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.cassandra.column.UDT;
import org.jnosql.diana.cassandra.column.UDTBuilder;

import java.util.Objects;

class CassandraUDTType implements FieldValue {

    private final String type;

    private final Object value;

    private final FieldRepresentation field;


    CassandraUDTType(String type, Object value, FieldRepresentation field) {
        this.value = value;
        this.type = type;
        this.field = Objects.requireNonNull(field, "field is required");
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public FieldRepresentation getField() {
        return field;
    }

    @Override
    public boolean isNotEmpty() {
        return value != null;
    }

    @Override
    public Document toDocument(DocumentEntityConverter converter, Converters converters) {
      throw new UnsupportedOperationException("Cassandra UDT type just supports Column");
    }

    @Override
    public Column toColumn(ColumnEntityConverter converter, Converters converters) {
        UDTBuilder builder = UDT.builder();
        return builder.withTypeName(type).withName(field.getName())
                .addAll(converter.toColumn(value).getColumns()).build();

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CassandraUDTType{");
        sb.append("type='").append(type).append('\'');
        sb.append(", value=").append(value);
        sb.append(", field=").append(field);
        sb.append('}');
        return sb.toString();
    }
}
