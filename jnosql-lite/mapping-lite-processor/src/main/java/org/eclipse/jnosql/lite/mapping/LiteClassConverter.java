/*
 *  Copyright (c) 2023 Otávio Santana and others
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
package org.eclipse.jnosql.lite.mapping;

import org.eclipse.jnosql.mapping.metadata.ClassConverter;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;

/**
 * The lite implementation of {@link ClassConverter}
 */
public final class LiteClassConverter implements ClassConverter {

    @Override
    public EntityMetadata apply(Class<?> aClass) {
        throw new UnsupportedOperationException("The lite implementation does not support this operation");
    }
}
