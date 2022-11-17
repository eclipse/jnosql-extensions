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

/**
 * An enumeration to show the available options to connect to the ArangoDB database.
 * It implements {@link Supplier}, where its it returns the property name that might be
 * overwritten by the system environment using Eclipse Microprofile or Jakarta Config API.
 *
 * @see jakarta.nosql.Settings
 */
public enum ArangoDBGraphConfigurations implements Supplier<String> {

    /**
     *
     */
    EDGE("jnosql.arangodb.graph.edge"),
    /**
     *
     */
    EDGE_CONFIGURATION("jnosql.arangodb.graph.edge.configuration"),
    /**
     *
     */
    VERTEX("jnosql.arangodb.graph.vertex"),
    /**
     *
     */
    GRAPH("jnosql.arangodb.graph.graph"),
    /**
     * The database host.
     */
    HOST("jnosql.arangodb.graph.host"),
    /**
     * The user's credential.
     */
    USER("jnosql.arangodb.graph.user"),
    /**
     * The password's credential.
     */
    PASSWORD("jnosql.arangodb.graph.password");

    private final String value;

    ArangoDBGraphConfigurations(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }
}
