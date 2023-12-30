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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

enum GraphMethodBuilder implements Function<MethodMetadata, List<String>> {
    NOT_SUPPORTED {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            return List.of("//There is no support for this method type yet.");
        }
    }, INSERT {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            return AnnotationOperationMethodBuilder.INSERT.apply(methodMetadata);
        }
    }, UPDATE {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            return AnnotationOperationMethodBuilder.UPDATE.apply(methodMetadata);
        }
    }, DELETE {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            return AnnotationOperationMethodBuilder.DELETE.apply(methodMetadata);
        }
    }, SAVE {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            return AnnotationOperationMethodBuilder.SAVE.apply(methodMetadata);
        }
    };

    static GraphMethodBuilder of(MethodMetadata metadata) {
        MethodMetadataOperationType operationType = MethodMetadataOperationType.of(metadata);
        return Arrays.stream(GraphMethodBuilder.values()).filter(c -> c.name().equals(operationType.name()))
                .findAny().orElse(NOT_SUPPORTED);
    }
}
