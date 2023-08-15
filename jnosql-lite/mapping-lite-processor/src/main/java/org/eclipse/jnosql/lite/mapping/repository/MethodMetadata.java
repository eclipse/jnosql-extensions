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

import jakarta.data.repository.Query;
import org.eclipse.jnosql.mapping.DatabaseType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

class MethodMetadata {

    private final String methodName;

    private final TypeElement returnElement;

    private final String returnType;

    private final List<Parameter> parameters;

    private final Query query;

    private final DatabaseType type;

    private MethodGenerator generator;

    private final String entityType;

    public MethodMetadata(String methodName, TypeElement returnElement, String returnType,
                          List<Parameter> parameters, Query query, DatabaseType type, String entityType) {

        this.methodName = methodName;
        this.returnElement = returnElement;
        this.returnType = returnType;
        this.parameters = parameters;
        this.query = query;
        this.type = type;
        this.entityType = entityType;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getParametersSignature() {
        return parameters.stream().map(p -> p.getType().toString() + " " + p.getName())
                .collect(joining(","));
    }

    void update(Function<MethodMetadata, MethodGenerator> methodGeneratorFactory) {
        this.generator = methodGeneratorFactory.apply(this);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<String> getSourceCode() {
        return this.generator.getLines();
    }

    public boolean hasReturn() {
        return this.generator.hasReturn();
    }

    public String getReturnValue() {
        return "result";
    }

    public DatabaseType getType() {
        return type;
    }

    public TypeElement getReturnElement() {
        return returnElement;
    }

    public Query getQuery() {
        return query;
    }

    public boolean hasQuery() {
        return query != null;
    }

    public String getEntityType() {
        return entityType;
    }
    public static MethodMetadata of(Element element, String entityType, DatabaseType type, ProcessingEnvironment processingEnv) {
        ElementKind kind = element.getKind();
        if (ElementKind.METHOD.equals(kind)) {
            ExecutableElement method = (ExecutableElement) element;
            String methodName = method.getSimpleName().toString();
            TypeElement returnElement = (TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType());
            String returnType = method.getReturnType().toString();
            List<Parameter> parameters = method.getParameters()
                    .stream()
                    .map(e -> Parameter.of(e, processingEnv))
                    .collect(Collectors.toList());

            Query query = method.getAnnotation(Query.class);
            return new MethodMetadata(methodName, returnElement, returnType, parameters, query, type, entityType);
        }
        return null;
    }

}
