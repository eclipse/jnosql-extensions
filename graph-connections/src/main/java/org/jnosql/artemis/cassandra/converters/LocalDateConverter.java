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
package org.jnosql.artemis.cassandra.converters;


import com.datastax.driver.core.LocalDate;
import jakarta.nosql.mapping.AttributeConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * The converter when the Cassandra type is time.
 * This attribute converter has support to:
 * <p>{@link Number}</p>
 * <p>{@link LocalDate}</p>
 * <p>{@link java.time.LocalDate}</p>
 * <p>{@link java.time.LocalDateTime}</p>
 * <p>{@link java.time.ZonedDateTime}</p>
 * <p>{@link Date}</p>
 * <p>{@link Calendar}</p>
 */
public class LocalDateConverter implements AttributeConverter<Object, LocalDate> {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    @Override
    public LocalDate convertToDatabaseColumn(Object attribute) {

        if (LocalDate.class.isInstance(attribute)) {
            return LocalDate.class.cast(attribute);
        }
        if (attribute == null) {
            return null;
        }
        if (Number.class.isInstance(attribute)) {
            return LocalDate.fromMillisSinceEpoch(Number.class.cast(attribute).longValue());
        }
        if (java.time.LocalDate.class.isInstance(attribute)) {

            Date date = Date.from(java.time.LocalDate.class.cast(attribute).atStartOfDay(ZONE_ID).toInstant());
            return LocalDate.fromMillisSinceEpoch(date.getTime());
        }
        if (LocalDateTime.class.isInstance(attribute)) {
            Date date = Date.from(LocalDateTime.class.cast(attribute).atZone(ZONE_ID).toInstant());
            return LocalDate.fromMillisSinceEpoch(date.getTime());
        }
        if (ZonedDateTime.class.isInstance(attribute)) {
            Date date = Date.from(ZonedDateTime.class.cast(attribute).toInstant());
            return LocalDate.fromMillisSinceEpoch(date.getTime());
        }
        if (Date.class.isInstance(attribute)) {
            return LocalDate.fromMillisSinceEpoch(Date.class.cast(attribute).getTime());
        }
        if (Calendar.class.isInstance(attribute)) {
            return LocalDate.fromMillisSinceEpoch(Calendar.class.cast(attribute).getTime().getTime());
        }
        throw new IllegalArgumentException("There is not support to: " + attribute.getClass());
    }

    @Override
    public Object convertToEntityAttribute(LocalDate dbData) {
        java.time.LocalDate localDate = java.time.LocalDate.of(dbData.getYear(), dbData.getMonth(), dbData.getDay());
        return Date.from(localDate.atStartOfDay(ZONE_ID).toInstant()).getTime();
    }
}
