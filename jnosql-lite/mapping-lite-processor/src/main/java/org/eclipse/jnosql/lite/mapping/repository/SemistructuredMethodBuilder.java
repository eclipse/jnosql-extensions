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
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

enum SemistructuredMethodBuilder implements Function<MethodMetadata, List<String>> {

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
            lines.add("org.eclipse.jnosql.mapping.PreparedStatement prepare = template.prepare(\"" + query.value() + "\")");
            for (Parameter parameter : metadata.getParameters()) {
                if (parameter.hasParam()) {
                    Param param = parameter.param();
                    lines.add("prepare.bind(\"" + param.value() + "\", " + parameter.name() + ")");
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
            lines.add("org.eclipse.jnosql.communication.query.method.DeleteMethodProvider deleteMethodFactory = " + SPACE +
                    "org.eclipse.jnosql.communication.query.method.DeleteMethodProvider.INSTANCE");
            lines.add("org.eclipse.jnosql.communication.query.method.DeleteByMethodQueryProvider supplier = " + SPACE +
                    " new org.eclipse.jnosql.communication.query.method.DeleteByMethodQueryProvider()");
            lines.add("org.eclipse.jnosql.communication.query.DeleteQuery delete = supplier.apply(\"" +
                    metadata.getMethodName() + "\", metadata.name())");
            lines.add("org.eclipse.jnosql.communication.semistructured.CommunicationObserverParser parser = " + SPACE +
                    "org.eclipse.jnosql.mapping.semistructured.query.RepositorySemistructuredObserverParser.of(metadata)");
            lines.add("org.eclipse.jnosql.communication.semistructured.DeleteQueryParams queryParams = " + SPACE +
                    "DELETE_PARSER.apply(delete, parser)");
            lines.add("org.eclipse.jnosql.communication.Params params = queryParams.params()");
            for (Parameter parameter : metadata.getParameters()) {
                lines.add("params.prefix(\"" + parameter.name() + "\", " + parameter.name() + ")");
            }
            lines.add("this.template.delete(queryParams.query())");
            return lines;
        }
    },NOT_SUPPORTED {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            return List.of("throw new UnsupportedOperationException(\"There is no support for this method type yet.\")");
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

    private static final String SPACE = "\n          ";

    private static void feedSelectQuery(MethodMetadata metadata, List<String> lines) {
        lines.add("org.eclipse.jnosql.communication.query.method.SelectMethodQueryProvider supplier = " + SPACE +
                "new org.eclipse.jnosql.communication.query.method.SelectMethodQueryProvider()");
        lines.add("org.eclipse.jnosql.communication.query.SelectQuery selectQuery = " + SPACE +
                "supplier.apply(\"" + metadata.getMethodName() + "\", metadata.name())");
        lines.add("org.eclipse.jnosql.communication.semistructured.CommunicationObserverParser parser = " + SPACE +
                "org.eclipse.jnosql.mapping.semistructured.query.RepositorySemistructuredObserverParser.of(metadata)");
        lines.add("org.eclipse.jnosql.communication.semistructured.QueryParams queryParams = " + SPACE +
                "SELECT_PARSER.apply(selectQuery, parser)");
        if (metadata.hasSpecialParameter()) {
            lines.add("SelectQuery query = " + SPACE +
                    " org.eclipse.jnosql.mapping.semistructured.query.DynamicQuery.of(new Object[]{" +
                    metadata.getSpecialParameter() +
                    "},  " + SPACE + "queryParams.query()).get()");
        } else {
            lines.add("SelectQuery query = queryParams.query()");
        }
        lines.add("org.eclipse.jnosql.communication.Params params = queryParams.params()");
        for (Parameter parameter : metadata.getQueryParams()) {
            lines.add("params.prefix(\"" + parameter.name() + "\", " + parameter.name() + ")");
        }
    }

    static SemistructuredMethodBuilder of(MethodMetadata metadata) {
        MethodMetadataOperationType operationType = MethodMetadataOperationType.of(metadata);
        return Arrays.stream(SemistructuredMethodBuilder.values()).filter(c -> c.name().equals(operationType.name()))
                .findAny().orElse(NOT_SUPPORTED);
    }
}
