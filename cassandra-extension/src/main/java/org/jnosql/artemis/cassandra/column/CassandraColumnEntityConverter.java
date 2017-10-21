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
import org.jnosql.artemis.column.AbstractColumnEntityConverter;
import org.jnosql.artemis.column.ColumnEntityConverter;
import org.jnosql.artemis.column.ColumnFieldValue;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.column.Column;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@ApplicationScoped
@Typed(CassandraColumnEntityConverter.class)
class CassandraColumnEntityConverter extends AbstractColumnEntityConverter implements ColumnEntityConverter {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Inject
    private Converters converters;


    @Override
    protected ClassRepresentations getClassRepresentations() {
        return classRepresentations;
    }

    @Override
    protected Reflections getReflections() {
        return reflections;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @Override
    protected <T> Consumer<String> feedObject(T instance, List<Column> columns, Map<String, FieldRepresentation> fieldsGroupByName) {
        return k -> {
            FieldRepresentation field = fieldsGroupByName.get(k);
            if (Objects.nonNull(field.getNativeField().getAnnotation(UDT.class))) {
                Optional<Column> column = columns.stream().filter(c -> c.getName().equals(k)).findFirst();
                setUDTField(instance, column, field);
            } else {
                super.feedObject(instance, columns, fieldsGroupByName).accept(k);
            }
        };
    }

    private <T> void setUDTField(T instance, Optional<Column> column, FieldRepresentation field) {
        if (column.isPresent() && org.jnosql.diana.cassandra.column.UDT.class.isInstance(column.get())) {
            org.jnosql.diana.cassandra.column.UDT udt = org.jnosql.diana.cassandra.column.UDT.class.cast(column.get());
            reflections.setValue(instance, field.getNativeField(), toEntity(field.getNativeField().getType(), udt.getColumns()));
        }
    }

    @Override
    protected ColumnFieldValue to(FieldRepresentation field, Object entityInstance) {
        Object value = reflections.getValue(entityInstance, field.getNativeField());
        UDT annotation = field.getNativeField().getAnnotation(UDT.class);
        if (Objects.isNull(annotation)) {
            return super.to(field, entityInstance);
        } else {
            return new CassandraUDTType(annotation.value(), value, field);
        }
    }
}