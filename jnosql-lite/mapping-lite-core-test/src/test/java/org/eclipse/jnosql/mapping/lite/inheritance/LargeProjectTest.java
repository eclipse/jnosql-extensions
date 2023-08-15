/*
 *  Copyright (c) 2022 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.lite.inheritance;

import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.LiteEntitiesMetadata;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.metadata.InheritanceMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LargeProjectTest {

    private EntitiesMetadata mappings;

    private EntityMetadata entityMetadata;

    @BeforeEach
    public void setUp() {
        this.mappings = new LiteEntitiesMetadata();
        this.entityMetadata = this.mappings.get(LargeProject.class);
    }

    @Test
    public void shouldGetName() {
        Assertions.assertEquals("Project", entityMetadata.name());
    }

    @Test
    public void shouldGetSimpleName() {
        Assertions.assertEquals(LargeProject.class.getSimpleName(), entityMetadata.simpleName());
    }

    @Test
    public void shouldGetClassName() {
        Assertions.assertEquals(LargeProject.class.getName(), entityMetadata.className());
    }

    @Test
    public void shouldGetClassInstance() {
        Assertions.assertEquals(LargeProject.class, entityMetadata.type());
    }

    @Test
    public void shouldGetId() {
        Optional<FieldMetadata> id = this.entityMetadata.id();
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    public void shouldCreateNewInstance() {
        LargeProject project = entityMetadata.newInstance();
        Assertions.assertNotNull(project);
        Assertions.assertTrue(project instanceof LargeProject);
    }

    @Test
    public void shouldGetFieldsName() {
        List<String> fields = entityMetadata.fieldsName();
        Assertions.assertEquals(2, fields.size());
        Assertions.assertTrue(fields.contains("name"));
        Assertions.assertTrue(fields.contains("budget"));
    }

    @Test
    public void shouldGetFieldsGroupByName() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        Assertions.assertNotNull(groupByName);
        Assertions.assertNotNull(groupByName.get("_id"));
        Assertions.assertNotNull(groupByName.get("budget"));
    }

    @Test
    public void shouldGetInheritanceMetadata() {
        InheritanceMetadata inheritance = this.entityMetadata.inheritance()
                .orElseThrow();
        Assertions.assertEquals("Large", inheritance.discriminatorValue());
        Assertions.assertEquals("size", inheritance.discriminatorColumn());
        Assertions.assertEquals(LargeProject.class, inheritance.entity());
        Assertions.assertEquals(Project.class, inheritance.parent());
    }

}
