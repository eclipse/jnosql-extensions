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
 * An enumeration to show the available options to connect to the Neo4J database.
 * It implements {@link Supplier}, where its it returns the property name that might be
 * overwritten by the system environment using Eclipse Microprofile or Jakarta Config API.
 *
 * @see jakarta.nosql.Settings
 */
public enum Neo4JGraphConfigurations implements Supplier<String> {

    /**
     * The database host
     */
    HOST("jnosql.neo4j.host"),
    /**
     * The user's credential
     */
    USER("jnosql.neo4j.user"),
    /**
     * The password's credential
     */
    PASSWORD("jnosql.neo4j.password");

    private final String value;

    Neo4JGraphConfigurations(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }
}
