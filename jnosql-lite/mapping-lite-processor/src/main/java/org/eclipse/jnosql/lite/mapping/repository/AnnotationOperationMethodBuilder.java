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

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
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
            TypeElement type = parameter.type();
            TypeMirror typeMirror = type.asType();
            Class<?> classType = getClass(typeMirror);
            var returnType = methodMetadata.getReturnType();
            if(returnType.equals(Void.TYPE.getName())) {
                return Collections.singletonList( "this.template.insert(" + parameter.name() + ")");
            } else if(returnType.equals(Integer.TYPE.getName())){
                return List.of("this.template.insert(" + parameter.name() + ")", "int result = 1");
            }
            return Collections.singletonList( "var result = this.template.insert(" + parameter.name() + ")");
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

    static Class<?> getClass(TypeMirror typeMirror) {
        try {
            return Class.forName(typeMirror.toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
