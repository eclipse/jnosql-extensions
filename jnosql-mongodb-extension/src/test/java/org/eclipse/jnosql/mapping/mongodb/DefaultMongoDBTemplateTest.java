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
 *   Alessandro Moscatelli
 */
package org.eclipse.jnosql.mapping.mongodb;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;
import org.bson.conversions.Bson;
import org.eclipse.jnosql.communication.mongodb.document.MongoDBDocumentManager;
import org.eclipse.jnosql.mapping.reflection.EntitiesMetadata;
import jakarta.nosql.tck.test.CDIExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.CriteriaQuery;
import jakarta.nosql.document.DocumentCondition;
import jakarta.nosql.document.DocumentQuery;
import java.util.Optional;
import org.eclipse.jnosql.mapping.mongodb.criteria.CriteriaQueryUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.EntityQueryResult;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.ExpressionQueryResult;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.ExpressionQueryResultRow;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.NumberExpression;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.StringExpression;

@CDIExtension
class DefaultMongoDBTemplateTest {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private EntitiesMetadata entities;

    @Inject
    private Converters converters;

    private MongoDBTemplate template;

    private MongoDBDocumentManager manager;

    @BeforeEach
    public void setUp() {
        this.manager = mock(MongoDBDocumentManager.class);
        Instance instance = mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultMongoDBTemplate(instance, converter, flow, entities, converters, persistManager);
    }

    @Test
    public void shouldReturnErrorOnDeleteMethod() {
        assertThrows(NullPointerException.class, () -> template.delete((String) null, null));
        assertThrows(NullPointerException.class, () -> template.delete("Collection", null));
        assertThrows(NullPointerException.class, () -> template.delete((String) null,
                eq("name", "Poliana")));

        assertThrows(NullPointerException.class, () -> template.delete(Person.class, null));
        assertThrows(NullPointerException.class, () -> template.delete((Class<Object>) null,
                eq("name", "Poliana")));
    }

    @Test
    public void shouldDeleteWithCollectionName() {
        Bson filter = eq("name", "Poliana");
        template.delete("Person", filter);
        Mockito.verify(manager).delete("Person", filter);
    }

    @Test
    public void shouldDeleteWithEntity() {
        Bson filter = eq("name", "Poliana");
        template.delete(Person.class, filter);
        Mockito.verify(manager).delete("Person", filter);
    }

    @Test
    public void shouldReturnErrorOnSelectMethod() {
        assertThrows(NullPointerException.class, () -> template.select((String) null, null));
        assertThrows(NullPointerException.class, () -> template.select("Collection", null));
        assertThrows(NullPointerException.class, () -> template.select((String) null,
                eq("name", "Poliana")));

        assertThrows(NullPointerException.class, () -> template.select(Person.class, null));
        assertThrows(NullPointerException.class, () -> template.select((Class<Object>) null,
                eq("name", "Poliana")));
    }

    @Test
    public void shouldSelectWithCollectionName() {
        DocumentEntity entity = DocumentEntity.of("Person", Arrays
                .asList(Document.of("_id", "Poliana"),
                        Document.of("age", 30)));
        Bson filter = eq("name", "Poliana");
        Mockito.when(manager.select("Person", filter))
                .thenReturn(Stream.of(entity));
        Stream<Person> stream = template.select("Person", filter);
        Assertions.assertNotNull(stream);
        Person poliana = stream.findFirst()
                .orElseThrow(() -> new IllegalStateException("There is an issue on the test"));

        Assertions.assertNotNull(poliana);
        assertEquals("Poliana", poliana.getName());
        assertEquals(30, poliana.getAge());
    }

    @Test
    public void shouldSelectWithEntity() {
        DocumentEntity entity = DocumentEntity.of("Person", Arrays
                .asList(Document.of("_id", "Poliana"),
                        Document.of("age", 30)));
        Bson filter = eq("name", "Poliana");
        Mockito.when(manager.select("Person", filter))
                .thenReturn(Stream.of(entity));
        Stream<Person> stream = template.select(Person.class, filter);
        Assertions.assertNotNull(stream);
        Person poliana = stream.findFirst()
                .orElseThrow(() -> new IllegalStateException("There is an issue on the test"));

        Assertions.assertNotNull(poliana);
        assertEquals("Poliana", poliana.getName());
        assertEquals(30, poliana.getAge());
    }

    @Test
    public void shouldReturnErrorOnAggregateMethod() {
        assertThrows(NullPointerException.class, () -> template.aggregate((String) null, null));
        assertThrows(NullPointerException.class, () -> template.aggregate("Collection", null));
        assertThrows(NullPointerException.class, () -> template.aggregate((String) null,
                Collections.singletonList(eq("name", "Poliana"))));

        assertThrows(NullPointerException.class, () -> template.aggregate(Person.class, null));
        assertThrows(NullPointerException.class, () -> template.aggregate((Class<Object>) null,
                Collections.singletonList(eq("name", "Poliana"))));
    }

    @Test
    public void shouldAggregateWithCollectionName() {
        List<Bson> predicates = Arrays.asList(
                Aggregates.match(eq("name", "Poliana")),
                Aggregates.group("$stars", Accumulators.sum("count", 1))
        );

        template.aggregate("Person", predicates);
        Mockito.verify(manager).aggregate("Person", predicates);
    }

    @Test
    public void shouldAggregateWithEntity() {
        List<Bson> predicates = Arrays.asList(
                Aggregates.match(eq("name", "Poliana")),
                Aggregates.group("$stars", Accumulators.sum("count", 1))
        );

        template.aggregate(Person.class, predicates);
        Mockito.verify(manager).aggregate("Person", predicates);
    }

    @Test
    public void shouldConvertCriterias() {

        CriteriaQuery<Person> personQuery = template.createQuery(Person.class);

        assertEquals(
                CriteriaQueryUtils.computeCondition(
                        personQuery.from().get(Person_.name).equal("Poliana").or(
                                personQuery.from().get(Person_.age).greaterThanOrEqualTo(17)
                        )
                ),
                DocumentCondition.or(
                        DocumentCondition.eq(
                                "name",
                                "Poliana"
                        ),
                        DocumentCondition.gte(
                                "age",
                                17
                        )
                )
        );

        CriteriaQuery<Music> musicQuery = template.createQuery(Music.class);

        assertEquals(
                CriteriaQueryUtils.computeCondition(
                        musicQuery.from().get(Music_.name).equal("SoFarSoGood").or(
                                musicQuery.from().get(Music_.year).greaterThanOrEqualTo(2022)
                        )
                ),
                DocumentCondition.or(
                        DocumentCondition.eq(
                                "name",
                                "SoFarSoGood"
                        ),
                        DocumentCondition.gte(
                                "year",
                                2022
                        )
                )
        );

    }

    @Test
    public void shouldSelectEntitiesWithCriteria() {

        DocumentEntity documentEntity = DocumentEntity.of(
                "Person",
                Arrays.asList(
                        Document.of("_id", "Poliana"),
                        Document.of("age", 17)
                )
        );

        Mockito.when(
                manager.select(
                        DocumentQuery.builder().from(
                                "Person"
                        ).where(
                                DocumentCondition.and(
                                        new DocumentCondition[]{
                                            DocumentCondition.or(
                                                    DocumentCondition.eq(
                                                            "name",
                                                            "Poliana"
                                                    ),
                                                    DocumentCondition.gte(
                                                            "age",
                                                            17
                                                    )
                                            )
                                        }
                                )
                        ).build()
                )
        ).thenReturn(
                Stream.of(
                        documentEntity
                )
        );

        CriteriaQuery<Person> personQuery = template.createQuery(Person.class);

        EntityQueryResult<Person> executeQuery = template.executeQuery(
                personQuery.select().where(
                        personQuery.from().get(
                                Person_.name
                        ).equal(
                                "Poliana"
                        ).or(
                                personQuery.from().get(
                                        Person_.age
                                ).greaterThanOrEqualTo(17)
                        )
                )
        );

        Optional<Person> findFirst = executeQuery.getEntities().findFirst();
        
        assertTrue(
                findFirst.isPresent()
        );
        assertEquals(
                converter.toDocument(
                        findFirst.get()
                ),
                documentEntity
        );

    }
    
    @Test
    public void shouldSelectProjectionsWithCriteria() {

        DocumentEntity documentEntity = DocumentEntity.of(
                "Person",
                Arrays.asList(
                        Document.of("_id", "Poliana"),
                        Document.of("age", 17)
                )
        );

        Mockito.when(
                manager.select(
                        DocumentQuery.builder(
                                "name",
                                "age"
                        ).from(
                                "Person"
                        ).where(
                                DocumentCondition.and(
                                        new DocumentCondition[]{
                                            DocumentCondition.or(
                                                    DocumentCondition.eq(
                                                            "name",
                                                            "Poliana"
                                                    ),
                                                    DocumentCondition.gte(
                                                            "age",
                                                            17
                                                    )
                                            )
                                        }
                                )
                        ).build()
                )
        ).thenReturn(
                Stream.of(
                        documentEntity
                )
        );

        CriteriaQuery<Person> personQuery = template.createQuery(Person.class);
        
        StringExpression<Person, Person> nameExpression = personQuery.from().get(
                Person_.name
        );
        NumberExpression<Person, Person, Integer> ageExpression = personQuery.from().get(
                Person_.age
        );

        ExpressionQueryResult<Person> executeQuery = template.executeQuery(
                personQuery.select(
                        nameExpression,
                        ageExpression
                ).where(
                        nameExpression.equal(
                                "Poliana"
                        ).or(
                                ageExpression.greaterThanOrEqualTo(17)
                        )
                )
        );

        Optional<ExpressionQueryResultRow<Person>> findFirst = executeQuery.getRows().findFirst();
        
        assertTrue(
                findFirst.isPresent()
        );
        assertEquals(
                findFirst.get().get(
                        nameExpression
                ),
                "Poliana"
        );
        assertEquals(
                findFirst.get().get(
                        ageExpression
                ),
                17
        );

    }

}
