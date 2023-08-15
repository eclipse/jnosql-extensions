/*
 *  Copyright (c) 2021 Otávio Santana and others
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
package org.eclipse.jnosql.lite.mapping.repository;

import jakarta.data.repository.Repository;
import org.eclipse.jnosql.lite.mapping.ValidationException;
import org.eclipse.jnosql.mapping.DatabaseType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.eclipse.jnosql.lite.mapping.ProcessorUtil.isTypeElement;
import static org.eclipse.jnosql.lite.mapping.repository.RepositoryUtil.findRepository;

class RepositoryElement {

    private final ProcessingEnvironment processingEnv;
    private final TypeElement element;
    private final String entityType;
    private final String keyType;
    private final String repositoryInterface;
    private final DatabaseType type;
    private final List<MethodMetadata> methods;

    public RepositoryElement(ProcessingEnvironment processingEnv, TypeElement element,
                             String entityType, String keyType,
                             String repositoryInterface,
                             DatabaseType type,
                             List<MethodMetadata> methods) {
        this.processingEnv = processingEnv;
        this.element = element;
        this.entityType = entityType;
        this.keyType = keyType;
        this.repositoryInterface = repositoryInterface;
        this.type = type;
        this.methods = methods;
    }

    public String getClassName() {
        return element.toString();
    }

    public DatabaseType getType() {
        return type;
    }

    public RepositoryMetadata getMetadata(DatabaseType type) {
        return switch (type) {
            case DOCUMENT -> new DocumentRepositoryMetadata(this);
            case COLUMN -> new ColumnRepositoryMetadata(this);
            case KEY_VALUE -> new KeyValueRepositoryMetadata(this);
            default -> throw new UnsupportedOperationException("There is not template to this database type: " + type);
        };
    }

    public String getEntityType() {
        return entityType;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getSimpleName() {
        return this.element.getSimpleName().toString();
    }

    public String getPackage() {
        String qualifiedName = this.element.getQualifiedName().toString();
        int index = qualifiedName.lastIndexOf('.');
        return qualifiedName.substring(0, index);
    }

    public String getRepository() {
        return repositoryInterface;
    }

    public List<MethodMetadata> getMethods() {
        return this.methods;
    }

    static RepositoryElement of(Element element, ProcessingEnvironment processingEnv, DatabaseType type) {
        if (isTypeElement(element)) {
            TypeElement typeElement = (TypeElement) element;
            Optional<TypeMirror> mirror = findRepository(typeElement.getInterfaces(), processingEnv);
            if (mirror.isPresent()) {
                TypeMirror typeMirror = mirror.get();
                List<String> parameters = RepositoryUtil.findParameters(typeMirror);
                String entityType = parameters.get(0);
                String keyType = parameters.get(1);
                String repositoryInterface = typeElement.getQualifiedName().toString();
                List<MethodMetadata> methods = typeElement.getEnclosedElements()
                        .stream()
                        .map(e -> MethodMetadata.of(e, entityType, type, processingEnv))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return new RepositoryElement(processingEnv, typeElement,
                        entityType, keyType, repositoryInterface, type, methods);
            }
        }
        throw new ValidationException("The interface " + element.toString() + "must extends " + Repository.class.getName());
    }

}
