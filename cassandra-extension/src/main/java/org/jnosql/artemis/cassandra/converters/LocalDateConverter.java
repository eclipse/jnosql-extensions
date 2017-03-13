/*
 * Copyright 2017 Otavio Santana and others
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jnosql.artemis.cassandra.converters;


import com.datastax.driver.core.LocalDate;
import org.jnosql.artemis.AttributeConverter;

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
        return dbData.getMillisSinceEpoch();
    }
}
