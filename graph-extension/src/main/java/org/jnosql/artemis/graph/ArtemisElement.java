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

import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.Value;

import java.util.List;

import static java.util.Objects.requireNonNull;

public interface ArtemisElement {

    String getKey();

    Value getValue();


    static ArtemisElement of(String key, Object value) throws NullPointerException {
        return new DefaultArtemisElement(key, value);
    }

    static ArtemisElement of(String key, Value value) throws NullPointerException {
        requireNonNull(value, "value is required");
        return new DefaultArtemisElement(key, value.get());
    }

    List<ArtemisElement> get(TypeReference<List<ArtemisElement>> typeReference);

    Object get();
}
