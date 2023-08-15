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

import java.util.List;

class ColumnMethodGenerator implements MethodGenerator {

    private final MethodMetadata metadata;

    private final ColumnMethodBuilder methodBuilder;

    ColumnMethodGenerator(MethodMetadata metadata) {
        this.metadata = metadata;
        this.methodBuilder = ColumnMethodBuilder.of(this.metadata);
    }

    @Override
    public List<String> getLines() {
        ColumnMethodBuilder methodBuilder = ColumnMethodBuilder.of(this.metadata);
        return methodBuilder.apply(this.metadata);
    }

    @Override
    public boolean hasReturn() {
        return !methodBuilder.equals(ColumnMethodBuilder.DELETE_BY);
    }
}
