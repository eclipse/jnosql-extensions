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
package org.eclipse.jnosql.lite.mapping;

import java.util.List;

public class ConstructorMetamodel {

    private final String packageName;

    private final String className;

    private final List<String> parameters;

    private ConstructorMetamodel(String packageName, String className, List<String> parameters) {
        this.packageName = packageName;
        this.className = className;
        this.parameters = parameters;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className + "ConstructorMetadata";
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getQualified() {
        return packageName + "." + getClassName();
    }


    public static ConstructorMetamodel of(String packageName, String className, List<String> parameters) {
        return new ConstructorMetamodel(packageName, className, parameters);
    }
}
