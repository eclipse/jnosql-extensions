/*
 *  Copyright (c) 2024 Otávio Santana and others
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
package org.eclipse.jnosql.metamodel.processor;

import jakarta.nosql.Column;
import jakarta.nosql.Embeddable;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

class FieldAnalyzer implements Supplier<List<FieldModel>> {

    private final Element field;
    private final ProcessingEnvironment processingEnv;
    private final TypeElement entity;

    private final String prefix;

    private final boolean group;

    FieldAnalyzer(Element field, ProcessingEnvironment processingEnv,
                  TypeElement entity) {
        this.field = field;
        this.processingEnv = processingEnv;
        this.entity = entity;
        this.prefix = null;
        this.group = true;

    }

    FieldAnalyzer(Element field, ProcessingEnvironment processingEnv,
                  TypeElement entity, String prefix, boolean group) {
        this.field = field;
        this.processingEnv = processingEnv;
        this.entity = entity;
        this.prefix = prefix;
        this.group = group;
    }

    @Override
    public List<FieldModel> get() {
        TypeMirror typeMirror = field.asType();
        String fieldName = field.getSimpleName().toString();
        var column = field.getAnnotation(Column.class);
        Id id = field.getAnnotation(Id.class);
        var name = getName(fieldName, column, id);
        var className = field.asType().toString();
        var constantName = fieldName.toUpperCase(Locale.US);

        Entity fieldEntity = null;
        Embeddable embeddable = null;

        if (typeMirror instanceof DeclaredType declaredType) {
            Element element = declaredType.asElement();
            fieldEntity = element.getAnnotation(Entity.class);
            embeddable = element.getAnnotation(Embeddable.class);
        }

        if (prefix != null) {

            if (this.group) {
                name = new StringJoiner(".").add(prefix)
                        .add(getName(fieldName, column, id))
                        .toString();
            }
            constantName = new StringJoiner("_")
                    .add(prefix.toUpperCase(Locale.US))
                    .add(fieldName.toUpperCase(Locale.US))
                    .toString();
            fieldName = new StringJoiner("_").add(prefix)
                    .add(fieldName)
                    .toString();
        }

        if (isCollectionElement(typeMirror)) {
            return getFieldEmbeddable(collectionElement(typeMirror), fieldName, name, true);
        } else if (isBasicField(fieldEntity, embeddable)) {
            return getBasicField(name, fieldName, constantName, className);
        } else if (isGroupEmbeddable(fieldEntity, embeddable)) {
            return getFieldEmbeddable((DeclaredType) typeMirror, fieldName, name, true);
        } else if (isFlatEmbeddable(embeddable)) {
            return getFieldEmbeddable((DeclaredType) typeMirror, fieldName, name, false);
        }
        return Collections.emptyList();
    }

    private List<FieldModel> getBasicField(String name, String fieldName, String constantName, String className) {
        return List.of(FieldModel.builder()
                .name(name)
                .fieldName(fieldName)
                .constantName(constantName)
                .className(className(className))
                .build());
    }

    private boolean isCollectionElement(TypeMirror field) {
        if (field instanceof DeclaredType declaredType) {
            var genericMirrorOptional = declaredType.getTypeArguments().stream().findFirst();
            return genericMirrorOptional.map(genericMirror -> {
                if (genericMirror instanceof DeclaredType genericDeclaredType) {
                    var genericElement = genericDeclaredType.asElement();
                    return genericElement.getAnnotation(Entity.class) != null
                            || genericElement.getAnnotation(Embeddable.class) != null;
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    private DeclaredType collectionElement(TypeMirror field) {
        if (field instanceof DeclaredType declaredType) {
            Optional<? extends TypeMirror> genericMirrorOptional = declaredType.getTypeArguments().stream().findFirst();
            TypeMirror genericMirror = genericMirrorOptional.orElseThrow();
            if (genericMirror instanceof DeclaredType genericDeclaredType) {
                return genericDeclaredType;
            }
        }
        throw new IllegalStateException("The field is not a collection: " + field);
    }


    private List<FieldModel> getFieldEmbeddable(DeclaredType declaredType, String fieldName, String name, boolean flat) {
        var element = declaredType.asElement();
        TypeElement typeElement = (TypeElement) element;
        List<FieldModel> elements = new ArrayList<>();

        elements.add(FieldModel.builder()
                .name(name)
                .fieldName(fieldName)
                .constantName(name.toUpperCase(Locale.US))
                .className("jakarta.data.metamodel.Attribute")
                .build());

        processingEnv.getElementUtils()
                .getAllMembers(typeElement)
                .stream()
                .filter(EntityProcessor.IS_FIELD.and(EntityProcessor.HAS_ANNOTATION))
                .map(f -> new FieldAnalyzer(f, processingEnv, typeElement, fieldName, flat))
                .map(FieldAnalyzer::get)
                .flatMap(List::stream)
                .forEach(elements::add);

        return elements;
    }


    private boolean isGroupEmbeddable(Entity fieldEntity, Embeddable embeddable) {
        return fieldEntity != null || isGroup(embeddable);
    }

    private boolean isGroup(Embeddable embeddable) {
        return embeddable != null && Embeddable.EmbeddableType.GROUPING.equals(embeddable.value());
    }

    private boolean isFlatEmbeddable(Embeddable embeddable) {
        return embeddable != null && Embeddable.EmbeddableType.FLAT.equals(embeddable.value());
    }

    private boolean isBasicField(Entity fieldEntity, Embeddable embeddable) {
        return fieldEntity == null && embeddable == null;
    }

    private String className(String className) {
        return switch (className) {
            case "java.lang.String" -> "org.eclipse.jnosql.metamodel.attributes.StringAttribute";
            case "java.lang.Boolean", "boolean" -> "org.eclipse.jnosql.metamodel.attributes.BooleanAttribute";
            default -> "org.eclipse.jnosql.metamodel.attributes.CriteriaAttribute";
        };
    }

    private String getName(String fieldName, Column column, Id id) {
        if (id == null) {
            return column.value().isBlank() ? fieldName : column.value();
        } else {
            return id.value().isBlank() ? fieldName : id.value();
        }
    }

}
