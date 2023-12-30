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
            var returnType = methodMetadata.getReturnType();
            boolean isVoid = returnType.equals(Void.TYPE.getName());
            boolean isInt = returnType.equals(Integer.TYPE.getName());
           if(parameter.isGeneric()){
               if(isVoid) {
                   return Collections.singletonList( "this.template.insert(" + parameter.name() + ")");
               } else if(isInt){
                   return List.of("this.template.insert(" + parameter.name() + ")",
                           "int result = (int)java.util.stream.StreamSupport.stream("+ parameter.name()+ ".spliterator(), false).count()");
               }
               return Collections.singletonList( "var result = this.template.insert(" + parameter.name() + ")");
           } else if(parameter.isArray()){
               if(isVoid) {

                   return Collections.singletonList("this.template.insert(java.util.Arrays.stream(" + parameter.name() + ").toList())");
               } else if(isInt){
                   return List.of("this.template.insert(java.util.Arrays.stream(" + parameter.name() + ").toList())",
                           "int result = " + parameter.name() + ".length");
               }
               return List.of("var insertResult = this.template.insert(java.util.Arrays.stream(" + parameter.name() + ").toList())",
                              "var result = java.util.stream.StreamSupport.stream(insertResult.spliterator(), false).toArray("+
                                      parameter.arrayType()+"::new)");
           }
            if(isVoid) {
                return Collections.singletonList( "this.template.insert(" + parameter.name() + ")");
            } else if(isInt){
                    return List.of("this.template.insert(" + parameter.name() + ")", "int result = 1");
            }
            return Collections.singletonList( "var result = this.template.insert(" + parameter.name() + ")");
        }
    }, SAVE {
        @Override
        public List<String> apply(MethodMetadata methodMetadata) {
            List<Parameter> parameters = methodMetadata.getParameters();
            if(parameters.size()!= 1){
                throw new IllegalStateException("The save method must have only one parameter");
            }
            Parameter parameter = parameters.get(0);
            var returnType = methodMetadata.getReturnType();
            boolean isVoid = returnType.equals(Void.TYPE.getName());
            boolean isInt = returnType.equals(Integer.TYPE.getName());
            if(parameter.isGeneric()){
                if(isVoid) {
                    return Collections.singletonList( "this.saveAll(" + parameter.name() + ")");
                } else if(isInt){
                    return List.of("this.saveAll(" + parameter.name() + ")",
                            "int result = (int)java.util.stream.StreamSupport.stream("+ parameter.name()+ ".spliterator(), false).count()");
                }
                return Collections.singletonList( "var result = this.saveAll(" + parameter.name() + ")");
            } else if(parameter.isArray()){
                if(isVoid) {

                    return Collections.singletonList("this.saveAll(java.util.Arrays.stream(" + parameter.name() + ").toList())");
                } else if(isInt){
                    return List.of("this.saveAll(java.util.Arrays.stream(" + parameter.name() + ").toList())",
                            "int result = " + parameter.name() + ".length");
                }
                return List.of("var saveResult = this.saveAll(java.util.Arrays.stream(" + parameter.name() + ").toList())",
                        "var result = java.util.stream.StreamSupport.stream(saveResult.spliterator(), false).toArray("+
                                parameter.arrayType()+"::new)");
            }
            if(isVoid) {
                return Collections.singletonList( "this.template.insert(" + parameter.name() + ")");
            } else if(isInt){
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
    }

}
