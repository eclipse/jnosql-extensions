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

import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Value;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.jnosql.artemis.reflection.FieldType.EMBEDDED;

/**
 * The default implementation {@link VertexConverter}
 */
class DefaultVertexConverter implements VertexConverter {


    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Inject
    private Converters converters;

    @Override
    public ArtemisVertex toVertex(Object entityInstance) throws NullPointerException {

        ClassRepresentation representation = classRepresentations.get(entityInstance.getClass());
        String label = representation.getName();

        List<FieldGraph> fields = representation.getFields().stream()
                .map(f -> to(f, entityInstance))
                .filter(FieldGraph::isNotEmpty).collect(toList());

        Optional<FieldGraph> id = fields.stream().filter(FieldGraph::isId).findFirst();

        ArtemisVertex vertex = id.map(f -> f.toElement(this, converters))
                .map(ArtemisProperty::get)
                .map(v -> ArtemisVertex.of(label, v))
                .orElse(ArtemisVertex.of(label));

        fields.stream().filter(FieldGraph::isNotId)
                .flatMap(f -> f.toElements(this, converters).stream())
                .forEach(vertex::add);


        return vertex;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, ArtemisVertex vertex) throws NullPointerException {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(vertex, "vertex is required");
        T entity = toEntity(entityClass, vertex.getProperties());

        feedId(vertex, entity);
        return entity;
    }


    @Override
    public <T> T toEntity(ArtemisVertex vertex) throws NullPointerException {
        requireNonNull(vertex, "vertex is required");
        ClassRepresentation representation = classRepresentations.findByName(vertex.getLabel());
        Class<T> entityClass = (Class<T>) representation.getClassInstance();
        T entity = toEntity(entityClass, vertex.getProperties());
        feedId(vertex, entity);
        return entity;
    }


    private <T> void feedId(ArtemisVertex vertex, T entity) {
        ClassRepresentation representation = classRepresentations.findByName(vertex.getLabel());
        Optional<FieldRepresentation> id = representation.getId();


        Optional<Value> vertexId = vertex.getId();
        if (vertexId.isPresent() && id.isPresent()) {
            Field fieldId = id.get().getNativeField();
            reflections.setValue(entity, fieldId, id.get().getValue(vertexId.get()));
        }
    }


    private <T> T convertEntity(List<ArtemisProperty> elements, ClassRepresentation representation, T instance) {

        Map<String, FieldRepresentation> fieldsGroupByName = representation.getFieldsGroupByName();
        List<String> names = elements.stream()
                .map(ArtemisProperty::getKey)
                .sorted()
                .collect(toList());
        Predicate<String> existField = k -> Collections.binarySearch(names, k) >= 0;

        fieldsGroupByName.keySet().stream()
                .filter(existField.or(k -> EMBEDDED.equals(fieldsGroupByName.get(k).getType())))
                .forEach(feedObject(instance, elements, fieldsGroupByName));

        return instance;
    }

    private <T> Consumer<String> feedObject(T instance, List<ArtemisProperty> elements,
                                            Map<String, FieldRepresentation> fieldsGroupByName) {
        return k -> {
            Optional<ArtemisProperty> element = elements
                    .stream()
                    .filter(c -> c.getKey().equals(k))
                    .findFirst();

            FieldRepresentation field = fieldsGroupByName.get(k);
            if (EMBEDDED.equals(field.getType())) {
                setEmbeddedField(instance, elements, field);
            } else {
                setSingleField(instance, element, field);
            }
        };
    }


    private <T> void setSingleField(T instance, Optional<ArtemisProperty> element, FieldRepresentation field) {
        Value value = element.get().getValue();
        Optional<Class<? extends AttributeConverter>> converter = field.getConverter();
        if (converter.isPresent()) {
            AttributeConverter attributeConverter = converters.get(converter.get());
            Object attributeConverted = attributeConverter.convertToEntityAttribute(value.get());
            reflections.setValue(instance, field.getNativeField(), field.getValue(Value.of(attributeConverted)));
        } else {
            reflections.setValue(instance, field.getNativeField(), field.getValue(value));
        }
    }

    private <T> void setEmbeddedField(T instance, List<ArtemisProperty> elements,
                                      FieldRepresentation field) {
        reflections.setValue(instance, field.getNativeField(), toEntity(field.getNativeField().getType(), elements));
    }

    private <T> T toEntity(Class<T> entityClass, List<ArtemisProperty> elements) {
        ClassRepresentation representation = classRepresentations.get(entityClass);
        T instance = reflections.newInstance(representation.getConstructor());
        return convertEntity(elements, representation, instance);
    }




    private FieldGraph to(FieldRepresentation field, Object entityInstance) {
        Object value = reflections.getValue(entityInstance, field.getNativeField());
        return FieldGraph.of(value, field);
    }

}
