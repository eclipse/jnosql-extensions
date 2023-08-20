/*
 *  Copyright (c) 2021 Otávio Santana and others
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
package org.eclipse.jnosql.lite.mapping.repository;

import java.util.Collections;
import java.util.List;

class GraphMethodGenerator implements MethodGenerator {

    GraphMethodGenerator() {
    }

    @Override
    public List<String> getLines() {
        return Collections.singletonList("throw new UnsupportedOperationException" +
                "(\"The Graph repository implementation does not support method query\")");
    }

    @Override
    public boolean hasReturn() {
        return true;
    }
}
