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

import java.math.BigDecimal;
import java.util.Objects;

public class Money {

    private final String currency;

    private final BigDecimal bigDecimal;

    public Money(String currency, BigDecimal bigDecimal) {
        this.currency = currency;
        this.bigDecimal = bigDecimal;
    }

    public static Money of(String dbData) {
        String[] values = dbData.split(" ");
        String currency = values[0];
        BigDecimal value = BigDecimal.valueOf(Double.parseDouble(values[1]));
        return new Money(currency, value);
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Money money = (Money) o;
        return Objects.equals(currency, money.currency) &&
                Objects.equals(bigDecimal, money.bigDecimal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, bigDecimal);
    }

    @Override
    public String toString() {
        return currency + " " + bigDecimal.toString();
    }
}
