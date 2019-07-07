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
package org.jnosql.artemis.graph.connections.converters;

import com.datastax.driver.core.LocalDate;
import jakarta.nosql.mapping.AttributeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalDateConverterTest {

    private AttributeConverter<Object, LocalDate> converter;

    @BeforeEach
    public void setUp() {
        converter = new LocalDateConverter();
    }

    @Test
    public void shouldConvertoNumber() {
        Date date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Number number = date.getTime();
        LocalDate localDate = converter.convertToDatabaseColumn(number);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), localDate.getDay());
        assertEquals(calendar.get(Calendar.YEAR), localDate.getYear());
        assertEquals(calendar.get(Calendar.MONTH) + 1, localDate.getMonth());
    }

    @Test
    public void shouldConvertoDate() {
        Date date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        LocalDate localDate = converter.convertToDatabaseColumn(date);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), localDate.getDay());
        assertEquals(calendar.get(Calendar.YEAR), localDate.getYear());
        assertEquals(calendar.get(Calendar.MONTH) + 1, localDate.getMonth());
    }

    @Test
    public void shouldConvertoCalendar() {

        Calendar calendar = Calendar.getInstance();
        LocalDate localDate = converter.convertToDatabaseColumn(calendar);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), localDate.getDay(), 2);
        assertEquals(calendar.get(Calendar.YEAR), localDate.getYear());
        assertEquals(calendar.get(Calendar.MONTH) + 1, localDate.getMonth());
    }

    @Test
    public void shouldConvertoLocalDate() {

        java.time.LocalDate date = java.time.LocalDate.now();
        LocalDate localDate = converter.convertToDatabaseColumn(date);
        assertEquals(date.getDayOfMonth(), localDate.getDay());
        assertEquals(date.getYear(), localDate.getYear());
        assertEquals(date.getMonthValue(), localDate.getMonth());
    }

    @Test
    public void shouldConvertoLocalDateCassandra() {

        LocalDate date = LocalDate.fromYearMonthDay(2017, 1, 9);
        LocalDate localDate = converter.convertToDatabaseColumn(date);
        assertEquals(date.getDay(), localDate.getDay());
        assertEquals(date.getYear(), localDate.getYear());
        assertEquals(date.getMonth(), localDate.getMonth());
    }

    @Test
    public void shouldConvertLocalDateTime() {

        LocalDateTime date = LocalDateTime.now();
        LocalDate localDate = converter.convertToDatabaseColumn(date);
        assertEquals(date.getDayOfMonth(), localDate.getDay(), 2);
        assertEquals(date.getYear(), localDate.getYear());
        assertEquals(date.getMonthValue(), localDate.getMonth());
    }

    @Test
    public void shouldConvertZonedDateTime() {

        ZonedDateTime date = ZonedDateTime.now();
        LocalDate localDate = converter.convertToDatabaseColumn(date);
        assertEquals(date.getDayOfMonth(), localDate.getDay(), 2);
        assertEquals(date.getYear(), localDate.getYear());
        assertEquals(date.getMonthValue(), localDate.getMonth());
    }

}