/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.cassandra.converters;

import com.datastax.driver.core.LocalDate;
import org.jnosql.artemis.AttributeConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class LocalDateConverterTest {

    private AttributeConverter<Object, LocalDate> converter;

    @Before
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
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), localDate.getDay());
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
        assertEquals(date.getDayOfMonth(), localDate.getDay());
        assertEquals(date.getYear(), localDate.getYear());
        assertEquals(date.getMonthValue(), localDate.getMonth());
    }

    @Test
    public void shouldConvertZonedDateTime() {

        ZonedDateTime date = ZonedDateTime.now();
        LocalDate localDate = converter.convertToDatabaseColumn(date);
        assertEquals(date.getDayOfMonth(), localDate.getDay());
        assertEquals(date.getYear(), localDate.getYear());
        assertEquals(date.getMonthValue(), localDate.getMonth());
    }

}