/*
 *  Copyright (c) 2020 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.lite;

import org.eclipse.jnosql.mapping.AttributeConverter;

import java.util.Objects;

public class MoneyConverter implements AttributeConverter<Money, String> {
    @Override
    public String convertToDatabaseColumn(Money attribute) {
        if (Objects.nonNull(attribute)) {
            return attribute.toString();
        }
        return null;
    }

    @Override
    public Money convertToEntityAttribute(String dbData) {
        if (Objects.nonNull(dbData)) {
            return Money.of(dbData);
        }
        return null;
    }
}
