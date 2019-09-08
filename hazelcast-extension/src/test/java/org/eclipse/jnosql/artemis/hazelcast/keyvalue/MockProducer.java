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
package org.eclipse.jnosql.artemis.hazelcast.keyvalue;


import com.hazelcast.query.Predicate;
import jakarta.nosql.Value;
import org.jnosql.diana.hazelcast.keyvalue.HazelcastBucketManager;

import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockProducer {


    @Produces
    public HazelcastBucketManager getManager() {
        HazelcastBucketManager manager = mock(HazelcastBucketManager.class);
        List<Value> people = asList(Value.of(new Person("Poliana", 25)),
                Value.of(new Person("Otavio", 28)));

        when(manager.sql(anyString())).thenReturn(people);
        when(manager.sql(anyString(), any(Map.class))).thenReturn(people);
        when(manager.sql(any(Predicate.class))).thenReturn(people);
        return manager;
    }

}
