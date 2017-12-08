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


import org.jnosql.diana.api.Value;
import org.jnosql.diana.hazelcast.key.HazelcastBucketManager;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

public class MockProducer {


    @Produces
    public HazelcastBucketManager getManager() {
        HazelcastBucketManager manager = Mockito.mock(HazelcastBucketManager.class);
        List<Value> people = asList(Value.of(new Person("Poliana", 25)),
                Value.of(new Person("Otavio", 28)));

        when(manager.query(Mockito.anyString())).thenReturn(people);
        return manager;
    }

}