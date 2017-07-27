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
package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.jnosql.diana.api.Value;

import java.util.List;
import java.util.Optional;

class DefaultEdgeEntity<IN, OUT> implements EdgeEntity<IN, OUT> {

    private final Edge edge;

    private final IN inbound;

    private final OUT outbound;

    DefaultEdgeEntity(Edge edge, IN inbound, OUT outbound) {
        this.edge = edge;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    @Override
    public Value getId() {
        return Value.of(edge.id());
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public IN getInbound() {
        return null;
    }

    @Override
    public OUT getOutbound() {
        return null;
    }

    @Override
    public List<ArtemisElement> getProperties() {
        return null;
    }

    @Override
    public void add(String key, Object value) throws NullPointerException {

    }

    @Override
    public void add(String key, Value value) throws NullPointerException {

    }

    @Override
    public void remove(String key) throws NullPointerException {

    }

    @Override
    public Optional<Value> get(String key) throws NullPointerException {
        return null;
    }
}
