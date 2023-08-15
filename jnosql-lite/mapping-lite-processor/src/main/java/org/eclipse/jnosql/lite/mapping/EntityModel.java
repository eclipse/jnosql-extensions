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

import java.util.List;

final class EntityModel extends BaseMappingModel {

    private final String packageName;

    private final String entity;

    private final String name;

    private final List<String> fields;

    private final boolean embedded;
    private final String inheritanceParameter;

    private final boolean notConcrete;

    private final boolean entityAnnotation;

    private final boolean hasInheritanceAnnotation;
    EntityModel(String packageName, String entity, String name,
                       List<String> fields, boolean embedded, boolean notConcrete,
                       String inheritanceParameter,
                       boolean entityAnnotation, boolean hasInheritanceAnnotation) {
        this.packageName = packageName;
        this.entity = entity;
        this.name = name;
        this.fields = fields;
        this.embedded = embedded;
        this.notConcrete = notConcrete;
        this.inheritanceParameter = inheritanceParameter;
        this.entityAnnotation = entityAnnotation;
        this.hasInheritanceAnnotation = hasInheritanceAnnotation;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getEntity() {
        return entity;
    }

    public String getEntityQualified() {
        return packageName + '.' + entity;
    }

    public String getClassName() {
        return entity + "EntityMetaData";
    }

    public String getQualified() {
        return packageName + "." + getClassName();
    }

    public String getName() {
        return name;
    }

    public List<String> getFields() {
        return fields;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public String getInheritanceParameter() {
        return inheritanceParameter;
    }

    public boolean isNotConcrete() {
        return notConcrete;
    }

    public boolean isEntityAnnotation() {
        return entityAnnotation;
    }

    public boolean isHasInheritanceAnnotation() {
        return hasInheritanceAnnotation;
    }

    @Override
    public String toString() {
        return "EntityModel{" +
                "packageName='" + packageName + '\'' +
                ", entity='" + entity + '\'' +
                ", name='" + name + '\'' +
                ", fields=" + fields +
                ", embedded=" + embedded +
                ", inheritanceParameter='" + inheritanceParameter + '\'' +
                ", notConcrete=" + notConcrete +
                ", entityAnnotation=" + entityAnnotation +
                '}';
    }
}
