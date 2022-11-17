/*
 *  Copyright (c) 2022 Eclipse Contribuitor
 * All rights reserved. This program and the accompanying materials
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *    You may elect to redistribute this code under either of these licenses.
 */

package org.eclipse.jnosql.mapping.graph.connections;

import java.util.function.Supplier;

public enum ArangoDBGraphConfigurations implements Supplier<String> {

    EDGE("jnosql.arangodb.graph.edge"),
    EDGE_CONFIGURATION("jnosql.arangodb.graph.edge.configuration"),
    VERTEX("jnosql.arangodb.graph.vertex"),
    GRAPH("jnosql.arangodb.graph.graph");

    private final String value;
    ArangoDBGraphConfigurations(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }
}
