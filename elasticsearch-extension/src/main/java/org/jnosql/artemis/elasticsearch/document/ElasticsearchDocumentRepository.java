/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.elasticsearch.document;


import org.elasticsearch.index.query.QueryBuilder;
import org.jnosql.artemis.document.DocumentRepository;

import java.util.List;

/**
 * A {@link DocumentRepository} to elasticsearch
 */
public interface ElasticsearchDocumentRepository extends DocumentRepository {

    /**
     * Find entities from {@link QueryBuilder}
     *
     * @param query the query
     * @param types the types
     * @return the objects from query
     * @throws NullPointerException when query is null
     */
    <T> List<T> find(QueryBuilder query, String... types);
}
