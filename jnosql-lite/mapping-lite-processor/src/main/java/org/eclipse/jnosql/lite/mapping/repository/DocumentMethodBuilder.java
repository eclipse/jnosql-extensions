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
import jakarta.data.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

enum DocumentMethodBuilder implements Function<MethodMetadata, List<String>> {

    METHOD_QUERY {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            feedSelectQuery(metadata, lines);
            MethodQueryRepositoryReturnType returnType = MethodQueryRepositoryReturnType.of(metadata);
            lines.addAll(returnType.apply(metadata));
            return lines;
        }
    }, ANNOTATION_QUERY {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            Query query = metadata.getQuery();
            lines.add("jakarta.nosql.PreparedStatement prepare = template.prepare(\"" + query.value() + "\")");
            for (Parameter parameter : metadata.getParameters()) {
                if (parameter.hasParam()) {
                    Param param = parameter.getParam();
                    lines.add("prepare.bind(\"" + param.value() + "\"," + parameter.getName() + ")");
                }
            }
            AnnotationQueryRepositoryReturnType returnType = AnnotationQueryRepositoryReturnType.of(metadata);
            lines.addAll(returnType.apply(metadata));
            return lines;
        }
    }, EXIST_BY {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            feedSelectQuery(metadata, lines);
            lines.add("Stream<" + metadata.getEntityType() + "> entities = this.template.select(query)");
            lines.add("boolean result = entities.findAny().isPresent()");
            return lines;
        }
    },COUNT_BY {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            feedSelectQuery(metadata, lines);
            lines.add("Stream<" + metadata.getEntityType() + "> entities = this.template.select(query)");
            lines.add("long result = entities.count()");
            return lines;
        }
    },DELETE_BY{
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            lines.add("org.eclipse.jnosql.communication.query.method.DeleteMethodProvider deleteMethodFactory = \n          " +
                    "org.eclipse.jnosql.communication.query.method.DeleteMethodProvider.INSTANCE");
            lines.add("org.eclipse.jnosql.communication.query.method.DeleteByMethodQueryProvider supplier = \n          " +
                    " new org.eclipse.jnosql.communication.query.method.DeleteByMethodQueryProvider()");
            lines.add("org.eclipse.jnosql.communication.query.DeleteQuery delete = supplier.apply(\"" +
                    metadata.getMethodName() + "\", metadata.name())");
            lines.add("org.eclipse.jnosql.communication.document.DocumentObserverParser parser = \n          " +
                    "org.eclipse.jnosql.mapping.document.query.RepositoryDocumentObserverParser.of(metadata)");
            lines.add("org.eclipse.jnosql.communication.document.DocumentDeleteQueryParams queryParams = \n          " +
                    "DELETE_PARSER.apply(delete, parser)");
            lines.add("org.eclipse.jnosql.communication.Params params = queryParams.params()");
            for (Parameter parameter : metadata.getParameters()) {
                lines.add("params.prefix(\"" + parameter.getName() + "\"," + parameter.getName() + ")");
            }
            lines.add("this.template.delete(queryParams.query())");
            return lines;
        }
    },NOT_SUPPORTED {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            return List.of("//There is no support for this method type yet.");
        }
    };

    private static void feedSelectQuery(MethodMetadata metadata, List<String> lines) {
        lines.add("org.eclipse.jnosql.communication.query.method.SelectMethodQueryProvider supplier = \n          " +
                "new org.eclipse.jnosql.communication.query.method.SelectMethodQueryProvider()");
        lines.add("org.eclipse.jnosql.communication.query.SelectQuery selectQuery = \n          " +
                "supplier.apply(\"" + metadata.getMethodName() + "\", metadata.name())");
        lines.add("org.eclipse.jnosql.communication.document.DocumentObserverParser parser = \n          " +
                "org.eclipse.jnosql.mapping.document.query.RepositoryDocumentObserverParser.of(metadata)");
        lines.add("org.eclipse.jnosql.communication.document.DocumentQueryParams queryParams = \n          " +
                "SELECT_PARSER.apply(selectQuery, parser)");
        lines.add("org.eclipse.jnosql.communication.document.DocumentQuery query = queryParams.query()");
        lines.add("org.eclipse.jnosql.communication.Params params = queryParams.params()");
        for (Parameter parameter : metadata.getParameters()) {
            lines.add("params.prefix(\"" + parameter.getName() + "\"," + parameter.getName() + ")");
        }
    }

    static DocumentMethodBuilder of(MethodMetadata metadata) {
        var methodName = metadata.getMethodName();
        if (methodName.startsWith("findBy")) {
            return METHOD_QUERY;
        } else if (methodName.startsWith("countBy")) {
            return COUNT_BY;
        } else if (methodName.startsWith("existsBy")) {
            return EXIST_BY;
        } else if (methodName.startsWith("deleteBy")) {
            return DELETE_BY;
        }  else if (metadata.hasQuery()) {
            return ANNOTATION_QUERY;
        }
        return NOT_SUPPORTED;
    }
}
