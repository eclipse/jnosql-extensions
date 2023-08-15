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

import jakarta.data.repository.Param;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

class Parameter {
    private final String name;
    private final Param param;
    private final TypeElement type;

    Parameter(String name, Param param, TypeElement type) {
        this.name = name;
        this.param = param;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public TypeElement getType() {
        return type;
    }

    public Param getParam() {
        return param;
    }

    public boolean hasParam() {
        return param != null;
    }

    public static Parameter of(VariableElement element, ProcessingEnvironment processingEnv) {
        String name = element.getSimpleName().toString();
        Param param = element.getAnnotation(Param.class);
        TypeElement type = (TypeElement) processingEnv.getTypeUtils().asElement(element.asType());
        return new Parameter(name, param, type);
    }
}
