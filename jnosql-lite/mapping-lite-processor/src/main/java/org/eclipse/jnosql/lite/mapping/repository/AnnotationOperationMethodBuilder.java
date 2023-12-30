/*
 *  Copyright (c) 2023 Otávio Santana and others
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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

enum AnnotationOperationMethodBuilder implements Function<MethodMetadata, List<String>> {

    INSERT {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            List<Parameter> parameters = methodMetadata.getParameters();
            if(parameters.size()!= 1){
                throw new IllegalStateException("The insert method must have only one parameter");
            }
            Parameter parameter = parameters.get(0);
            return Collections.singletonList( "this.template.insert(" + parameter.getName() + ")");
        }
    },
    UPDATE {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            return null;
        }
    },

    DELETE {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            return null;
        }
    },
    SAVE {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            return null;
        }
    };
}
