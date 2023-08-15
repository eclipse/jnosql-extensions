/*
 *  Copyright (c) 2020 Otávio Santana and others
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
package org.eclipse.jnosql.lite.mapping;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import org.eclipse.jnosql.mapping.Convert;
import org.eclipse.jnosql.mapping.Embeddable;
import org.eclipse.jnosql.mapping.metadata.MappingType;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class FieldAnalyzer implements Supplier<String> {

    private static final String DEFAULT_TEMPLATE = "fieldmetadata.mustache";
    private static final String GENERIC_TEMPLATE = "fieldgenericmetadata.mustache";
    private static final Predicate<Element> IS_METHOD = el -> el.getKind() == ElementKind.METHOD;
    private static final Function<Element, String> ELEMENT_TO_STRING = el -> el.getSimpleName().toString();
    private static final String NULL = "null";
    private final Element field;
    private final Mustache template;

    private final Mustache genericTemplate;
    private final ProcessingEnvironment processingEnv;
    private final TypeElement entity;

    FieldAnalyzer(Element field, ProcessingEnvironment processingEnv,
                  TypeElement entity) {
        this.field = field;
        this.processingEnv = processingEnv;
        this.entity = entity;
        this.template = createTemplate(DEFAULT_TEMPLATE);
        this.genericTemplate = createTemplate(GENERIC_TEMPLATE);
    }

    @Override
    public String get() {
        FieldModel metadata = getMetaData();
        Filer filer = processingEnv.getFiler();
        JavaFileObject fileObject = getFileObject(metadata, filer);
        try (Writer writer = fileObject.openWriter()) {
            if(NULL.equals(metadata.getElementType())){
                template.execute(writer, metadata);
            } else {
                genericTemplate.execute(writer, metadata);
            }
        } catch (IOException exception) {
            throw new ValidationException("An error to compile the class: " +
                    metadata.getQualified(), exception);
        }
        return metadata.getQualified();
    }

    private JavaFileObject getFileObject(FieldModel metadata, Filer filer) {
        try {
            return filer.createSourceFile(metadata.getQualified(), entity);
        } catch (IOException exception) {
            throw new ValidationException("An error to create the class: " +
                    metadata.getQualified(), exception);
        }

    }

    private FieldModel getMetaData() {
        final String fieldName = field.getSimpleName().toString();
        final Predicate<Element> validName = el -> el.getSimpleName().toString()
                .contains(ProcessorUtil.capitalize(fieldName));
        final Predicate<String> hasGetterName = el -> el.equals("get" + ProcessorUtil.capitalize(fieldName));
        final Predicate<String> hasSetterName = el -> el.equals("set" + ProcessorUtil.capitalize(fieldName));
        final Predicate<String> hasIsName = el -> el.equals("is" + ProcessorUtil.capitalize(fieldName));

        final List<Element> accessors = processingEnv.getElementUtils()
                .getAllMembers(entity).stream()
                .filter(validName.and(IS_METHOD).and(EntityProcessor.HAS_ACCESS))
                .collect(Collectors.toList());

        final TypeMirror typeMirror = field.asType();
        String className;
        String elementType = NULL;
        boolean embeddable = false;
        String collectionInstance = CollectionUtil.DEFAULT;
        MappingType mappingType = MappingType.DEFAULT;
        String supplierElement = null;


        if (typeMirror instanceof DeclaredType declaredType) {
            Element element = declaredType.asElement();
            className = element.toString();
            supplierElement = typeMirror.toString();
            embeddable = isEmbeddable(declaredType);
           collectionInstance = CollectionUtil.INSTANCE.apply(className);
           elementType = elementType(declaredType);
           mappingType = of(element, collectionInstance, collectionInstance);

        } else {
            className = typeMirror.toString();
        }
        Column column = field.getAnnotation(Column.class);
        Id id = field.getAnnotation(Id.class);
        Convert convert = field.getAnnotation(Convert.class);

        List<ValueAnnotationModel> valueAnnotationModels = new ArrayList<>();
        for (AnnotationMirror annotationMirror : field.getAnnotationMirrors()) {
            DeclaredType annotationType = annotationMirror.getAnnotationType();
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
            Predicate<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> isValueMethod =
                    e -> e.getKey().toString().equals("value()");

            List<String> defaultJNoSQL = List.of(Column.class.getName(),
                    Id.class.getName(),
                    Convert.class.getName());
            Predicate<Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> isNotDefaultAnnotation = e -> !
                    defaultJNoSQL.contains(annotationType.toString());
            elementValues.entrySet().stream().filter(isNotDefaultAnnotation.and(isValueMethod)).findFirst().ifPresent(e -> {
                String key = annotationType.toString()+".class";
                String value = e.getValue().getValue().toString();
                ValueAnnotationModel valueAnnotationModel = new ValueAnnotationModel(key, value);
                valueAnnotationModels.add(valueAnnotationModel);
            });
        }

        final boolean isId = id != null;
        final String packageName = ProcessorUtil.getPackageName(entity);
        final String entityName = ProcessorUtil.getSimpleNameAsString(this.entity);
        final String name = getName(fieldName, column, id);

        final String getMethod = accessors.stream()
                .map(ELEMENT_TO_STRING)
                .filter(hasGetterName)
                .findFirst().orElseThrow(generateGetterError(fieldName, packageName, entityName,
                        "There is not valid getter method to the field: "));
        final String setMethod = accessors.stream()
                .map(ELEMENT_TO_STRING)
                .filter(hasSetterName.or(hasIsName))
                .findFirst().orElseThrow(generateGetterError(fieldName, packageName, entityName,
                        "There is not valid setter method to the field: "));

        return FieldModel.builder()
                .packageName(packageName)
                .name(name)
                .type(className)
                .entity(entityName)
                .reader(getMethod)
                .writer(setMethod)
                .fieldName(fieldName)
                .id(isId)
                .elementType(elementType)
                .converter(convert)
                .embeddable(embeddable)
                .mappingType("MappingType." + mappingType.name())
                .valueByAnnotation(valueAnnotationModels)
                .collectionInstance(collectionInstance)
                .supplierElement(supplierElement)
                .build();
    }

    private String getName(String fieldName, Column column, Id id) {
        if (id == null) {
            return column.value().isBlank() ? fieldName : column.value();
        } else {
            return id.value().isBlank() ? fieldName : id.value();
        }
    }

    private Supplier<ValidationException> generateGetterError(String fieldName, String packageName, String entity, String s) {
        return () -> new ValidationException(s + fieldName + " in the class: " + packageName + "." + entity);
    }

    private Mustache createTemplate(String template) {
        MustacheFactory factory = new DefaultMustacheFactory();
        return factory.compile(template);
    }


    private static MappingType of(TypeMirror type, String collection, String fieldType) {

        if (type.getAnnotation(Embeddable.class) != null) {
            return MappingType.EMBEDDED;
        }
        if (type.getAnnotation(Entity.class) != null) {
            return MappingType.ENTITY;
        }
        if (!collection.equals(CollectionUtil.DEFAULT)) {
            return MappingType.COLLECTION;
        }
        if (fieldType.equals("java.util.Map")) {
            return MappingType.MAP;
        }
        return MappingType.DEFAULT;
    }

    private static MappingType of(Element element, String collection, String fieldType) {
        if (element.getAnnotation(Embeddable.class) != null) {
            return MappingType.EMBEDDED;
        }
        if (element.getAnnotation(Entity.class) != null) {
            return MappingType.ENTITY;
        }
        if (!collection.equals(CollectionUtil.DEFAULT)) {
            return MappingType.COLLECTION;
        }
        if (fieldType.equals("java.util.Map")) {
            return MappingType.MAP;
        }
        return MappingType.DEFAULT;
    }

    private String elementType(DeclaredType declaredType) {
        Optional<? extends TypeMirror> genericMirrorOptional = declaredType.getTypeArguments().stream().findFirst();
        if (genericMirrorOptional.isPresent()) {
            TypeMirror genericMirror = genericMirrorOptional.get();
            return genericMirror + ".class";
        } else {
            return NULL;
        }
    }

    private boolean isEmbeddable(DeclaredType declaredType) {
        return declaredType.getTypeArguments().stream()
                .filter(DeclaredType.class::isInstance).map(DeclaredType.class::cast)
                .map(DeclaredType::asElement).findFirst().map(e -> e.getAnnotation(Embeddable.class) != null ||
                e.getAnnotation(Entity.class) != null).orElse(false);
    }

}
