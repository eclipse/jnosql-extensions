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
package org.jnosql.artemis.cassandra.column;


import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;

@Entity
public class History {

    @Column
    private String name;

    @Column
    private Number number;

    @Column
    private LocalDate localDate;

    @Column
    private LocalDateTime localDateTime;

    @Column
    private Calendar calendar;

    @Column
    private ZonedDateTime zonedDateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
}
