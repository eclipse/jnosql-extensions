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
package org.jnosql.artemis.arangodb.document;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

public class CDIContext {

    public static final CDIContext INSTANCE = new CDIContext();

    private final SeContainer container;

    private CDIContext() {
        this.container = SeContainerInitializer.newInstance().initialize();
        Runtime.getRuntime().addShutdownHook(new Thread(container::close));
    }

    public <T> T getBean(Class<T> type) {
        return container.select(type).get();
    }
}