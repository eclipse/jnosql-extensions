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
package org.jnosql.artemis.cassandra.column;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.artemis.column.ColumnFieldValue;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.diana.api.column.Column;
import org.jnosql.diana.cassandra.column.UDT;

import java.util.Objects;

class CassandraUDTType implements ColumnFieldValue {

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
    public Column toColumn(ColumnEntityConverter converter, Converters converters) {
        return UDT.builder(type)
                .withName(field.getName())
                .addUDT(converter.toColumn(value).getColumns())
                .build();
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
