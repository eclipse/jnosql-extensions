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
package org.jnosql.artemis.hazelcast.key;

import com.hazelcast.query.Predicate;
import org.jnosql.artemis.key.AbstractKeyValueTemplate;
import org.jnosql.artemis.key.KeyValueEntityConverter;
import org.jnosql.artemis.key.KeyValueWorkflow;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.hazelcast.key.HazelcastBucketManager;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Typed(HazelcastTemplate.class)
class DefaultHazelcastTemplate extends AbstractKeyValueTemplate implements HazelcastTemplate {

    @Inject
    private Instance<HazelcastBucketManager> manager;

    @Inject
    private KeyValueWorkflow flow;

    @Inject
    private KeyValueEntityConverter converter;


    @Override
    public <T> Collection<T> sql(String query) {
        return manager.get().sql(query).stream().map(v -> (T) v.get()).collect(Collectors.toList());
    }

    @Override
    public <T> Collection<T> sql(String query, Map<String, Object> params) {
        return manager.get().sql(query, params).stream().map(v -> (T) v.get()).collect(Collectors.toList());
    }

    @Override
    public <K, V> Collection<V> sql(Predicate<K, V> predicate) {
        return manager.get().sql(predicate).stream().map(v -> (V) v.get()).collect(Collectors.toList());
    }

    @Override
    protected KeyValueEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected BucketManager getManager() {
        return manager.get();
    }

    @Override
    protected KeyValueWorkflow getFlow() {
        return flow;
    }
}
