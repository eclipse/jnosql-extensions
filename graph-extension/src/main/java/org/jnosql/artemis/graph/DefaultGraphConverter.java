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

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.Value;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.jnosql.artemis.reflection.FieldType.EMBEDDED;

@ApplicationScoped
class DefaultGraphConverter implements GraphConverter {

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Inject
    private Converters converters;


    @Override
    public <T> Vertex toVertex(T entity) {


        return null;
    }

    @Override
    public <T> T toEntity(Vertex vertex) {
        requireNonNull(vertex, "vertex is required");
        ClassRepresentation representation = classRepresentations.findByName(vertex.label());

        List<Property> properties = vertex.keys().stream().map(k -> Property.of(k, vertex.value(k))).collect(toList());
        T entity = toEntity((Class<T>) representation.getClassInstance(), properties);
        feedId(vertex, entity);
        return entity;
    }

    @Override
    public EdgeEntity toEdgeEntity(Edge edge) {
        return null;
    }

    @Override
    public Edge toEdge(EdgeEntity edge) {
        return null;
    }

    private <T> void feedId(Vertex vertex, T entity) {
        ClassRepresentation representation = classRepresentations.get(entity.getClass());
        Optional<FieldRepresentation> id = representation.getId();


        Object vertexId = vertex.id();
        if (Objects.nonNull(vertexId) && id.isPresent()) {
            FieldRepresentation fieldRepresentation = id.get();
            Field fieldId = fieldRepresentation.getNativeField();
            if (fieldRepresentation.getConverter().isPresent()) {
                AttributeConverter attributeConverter = converters.get(fieldRepresentation.getConverter().get());
                Object attributeConverted = attributeConverter.convertToEntityAttribute(vertexId);
                reflections.setValue(entity, fieldId, fieldRepresentation.getValue(Value.of(attributeConverted)));
            } else {
                reflections.setValue(entity, fieldId, fieldRepresentation.getValue(Value.of(vertexId)));
            }

        }
    }

    private <T> T toEntity(Class<T> entityClass, List<Property> properties) {
        ClassRepresentation representation = classRepresentations.get(entityClass);
        T instance = reflections.newInstance(representation.getConstructor());
        return convertEntity(properties, representation, instance);
    }

    private <T> T convertEntity(List<Property> elements, ClassRepresentation representation, T instance) {

        Map<String, FieldRepresentation> fieldsGroupByName = representation.getFieldsGroupByName();
        List<String> names = elements.stream()
                .map(Property::getKey)
                .sorted()
                .collect(toList());
        Predicate<String> existField = k -> Collections.binarySearch(names, k) >= 0;

        fieldsGroupByName.keySet().stream()
                .filter(existField.or(k -> EMBEDDED.equals(fieldsGroupByName.get(k).getType())))
                .forEach(feedObject(instance, elements, fieldsGroupByName));

        return instance;
    }

    private <T> Consumer<String> feedObject(T instance, List<Property> elements,
                                            Map<String, FieldRepresentation> fieldsGroupByName) {
        return k -> {
            Optional<Property> element = elements
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

    private <T> void setSingleField(T instance, Optional<Property> element, FieldRepresentation field) {
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

    private <T> void setEmbeddedField(T instance, List<Property> elements,
                                      FieldRepresentation field) {
        reflections.setValue(instance, field.getNativeField(), toEntity(field.getNativeField().getType(), elements));
    }
}
