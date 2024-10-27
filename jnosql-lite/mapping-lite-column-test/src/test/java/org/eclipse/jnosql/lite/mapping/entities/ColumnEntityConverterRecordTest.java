/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.lite.mapping.entities;

import jakarta.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.communication.TypeReference;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.semistructured.CommunicationEntity;
import org.eclipse.jnosql.communication.semistructured.Element;
import org.eclipse.jnosql.lite.mapping.entities.record.Guest;
import org.eclipse.jnosql.lite.mapping.entities.record.Hotel;
import org.eclipse.jnosql.lite.mapping.entities.record.HotelManager;
import org.eclipse.jnosql.lite.mapping.entities.record.Room;
import org.eclipse.jnosql.lite.mapping.metadata.LiteEntitiesMetadata;
import org.eclipse.jnosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.column.spi.ColumnExtension;
import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.core.spi.EntityMetadataExtension;
import org.eclipse.jnosql.mapping.semistructured.EntityConverter;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@EnableAutoWeld
@AddPackages(value = {Converters.class, EntityConverter.class, ColumnTemplate.class})
@AddPackages(LiteEntitiesMetadata.class)
@AddExtensions({EntityMetadataExtension.class, ColumnExtension.class})
public class ColumnEntityConverterRecordTest {

    @Inject
    private EntityConverter converter;

    @Test
    void shouldConvertToCommunication() {
        Room room = new Room(1231, new Guest("Ada", "12321", asList("123", "321")));

        CommunicationEntity entity = converter.toCommunication(room);

        SoftAssertions.assertSoftly(s -> {
            s.assertThat(entity.name()).isEqualTo("Room");
            s.assertThat(entity.find("_id").orElseThrow().get()).isEqualTo(1231);
            s.assertThat(entity.find("name")).isPresent().get().isEqualTo(Element.of("name", "Ada"));
            s.assertThat(entity.find("document")).isPresent().get().isEqualTo(Element.of("document","12321"));
            s.assertThat(entity.find("phones")).isPresent().get().isEqualTo( Element.of("phones",asList("123", "321")));
        });
    }


    @Test
    void shouldConvertToDocumentEntity() {
        var entity = CommunicationEntity.of("Room");
        entity.add("_id", 1231);
        entity.add("name", "Ada");
        entity.add("document", "12321");
        entity.add("phones", List.of("123", "321"));

        Room room = converter.toEntity(entity);

        SoftAssertions.assertSoftly(s -> {
            s.assertThat(room.number()).isEqualTo(1231);
        });
    }


    @Test
    void shouldConvertToCommunicationGroup() {
        Hotel hotel = new Hotel("123", new HotelManager("Ada", "12321"));

        CommunicationEntity entity = converter.toCommunication(hotel);

        SoftAssertions.assertSoftly(s -> {
            s.assertThat(entity.name()).isEqualTo("Hotel");
            s.assertThat(entity.find("_id").orElseThrow().get()).isEqualTo("123");
            s.assertThat(entity.find("manager")).isNotEmpty();
        });
    }

    @Test
    void shouldConvertToDocumentEntityGroup() {
        var entity = CommunicationEntity.of("Hotel");
        entity.add("_id", "123");
        entity.add("manager", List.of(Element.of("name", "Ada"), Element.of("document", "12321")));

        Hotel room = converter.toEntity(entity);

        SoftAssertions.assertSoftly(s -> {
            s.assertThat(room.number()).isEqualTo("123");
            s.assertThat(room.manager().name()).isEqualTo("Ada");
            s.assertThat(room.manager().document()).isEqualTo("12321");
        });
    }


}
