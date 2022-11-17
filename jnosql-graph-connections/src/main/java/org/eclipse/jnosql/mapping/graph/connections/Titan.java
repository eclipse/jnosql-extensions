/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.mapping.graph.connections;

import com.thinkaurelius.titan.core.TitanFactory;
import jakarta.nosql.Settings;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.eclipse.jnosql.mapping.graph.GraphConfiguration;

import java.util.Objects;

/**
 * Creates the connection to {@link Graph} using Titan.
 */
public class Titan implements GraphConfiguration {

    @Override
    public Graph apply(Settings settings) {
        Objects.requireNonNull(settings, "settings is required");
        Configuration configuration = new BaseConfiguration();
        for (String key : settings.keySet()) {
            configuration.addProperty(key, settings.get(key, String.class).orElseThrow());
        }
        return TitanFactory.open(configuration);
    }
}
