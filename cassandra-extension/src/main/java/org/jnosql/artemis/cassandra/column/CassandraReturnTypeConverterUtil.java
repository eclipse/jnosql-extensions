/*
 *  Copyright (c) 2017 Otávio Santana and others
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
package org.jnosql.artemis.cassandra.column;


import com.datastax.driver.core.ConsistencyLevel;
import org.jnosql.diana.api.column.ColumnQuery;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CassandraReturnTypeConverterUtil {

    private CassandraReturnTypeConverterUtil() {
    }

    static Object returnObject(List result, Class typeClass, Method method) {
        Class<?> returnType = method.getReturnType();

        if (typeClass.equals(returnType)) {
            if (!result.isEmpty()) {
                return result.get(0);
            } else {
                return null;
            }

        } else if (Optional.class.equals(returnType)) {
            return result.stream().findFirst();
        } else if (List.class.equals(returnType)
                || Iterable.class.equals(returnType)
                || Collection.class.equals(returnType)) {
            return result;
        } else if (Set.class.equals(returnType)) {
            return result.stream().collect(Collectors.toSet());
        } else if (Queue.class.equals(returnType)) {
            return result.stream().collect(Collectors.toCollection(PriorityQueue::new));
        } else if (Stream.class.equals(returnType)) {
            return result.stream();
        }

        return result;
    }

    static Object returnObject(ColumnQuery query, CassandraTemplate repository, Class typeClass, Method method, ConsistencyLevel level) {
        Class<?> returnType = method.getReturnType();

        if (typeClass.equals(returnType)) {
            List<Object> result = repository.find(query, level);
            if (!result.isEmpty()) {
                return result.get(0);
            } else {
                return null;
            }

        } else if (Optional.class.equals(returnType)) {
            List<Object> result = repository.find(query, level);
            return result.stream().findFirst();
        } else if (List.class.equals(returnType)
                || Iterable.class.equals(returnType)
                || Collection.class.equals(returnType)) {
            return repository.find(query, level);
        } else if (Set.class.equals(returnType)) {
            return repository.find(query, level).stream().collect(Collectors.toSet());
        } else if (Queue.class.equals(returnType)) {
            return repository.find(query, level).stream().collect(Collectors.toCollection(PriorityQueue::new));
        } else if (Stream.class.equals(returnType)) {
            return repository.find(query, level).stream();
        }

        return repository.find(query, level);
    }
}
