/*
 *  Copyright (c) 2024 Otávio Santana and others
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
package org.eclipse.jnosql.metamodel;

import jakarta.data.Sort;
import org.assertj.core.api.Assertions;
import org.eclipse.jnosql.communication.semistructured.CriteriaCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class NoSQLAttributeTest {

    public static final String NAME = "testAttribute";
    private NoSQLAttribute<String> attribute;

    @BeforeEach
    public void setUp() {
        attribute = new NoSQLAttribute<>(NAME);
    }

    @Test
    public void shouldReturnAscendingSortWhenAscIsCalled() {
        Sort<String> result = attribute.asc();
        assertThat(result).isEqualTo(Sort.asc(NAME));
    }

    @Test
    public void shouldReturnDescendingSortWhenDescIsCalled() {
        Sort<String> result = attribute.desc();
        assertThat(result).isEqualTo(Sort.desc(NAME));
    }

    @Test
    public void shouldReturnTrueConditionWhenIsTrueIsCalled() {
        CriteriaCondition condition = attribute.isTrue();
        assertThat(condition).isEqualTo(CriteriaCondition.eq(NAME, true));
    }

    @Test
    public void shouldReturnFalseConditionWhenIsFalseIsCalled() {
        CriteriaCondition condition = attribute.isFalse();
        assertThat(condition).isEqualTo(CriteriaCondition.eq(NAME, false));
    }

    @Test
    public void shouldReturnEqualConditionWhenEqIsCalledWithNonNullValue() {
        CriteriaCondition condition = attribute.eq("someValue");
        assertThat(condition).isEqualTo(CriteriaCondition.eq(NAME, "someValue"));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenEqIsCalledWithNull() {
        assertThatThrownBy(() -> attribute.eq(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldReturnGreaterThanConditionWhenGtIsCalled() {
        CriteriaCondition condition = attribute.gt("someValue");
        assertThat(condition).isEqualTo(CriteriaCondition.gt(NAME, "someValue"));
    }

    @Test
    public void shouldReturnGreaterThanOrEqualToConditionWhenGteIsCalled() {
        CriteriaCondition condition = attribute.gte("someValue");
        assertThat(condition).isEqualTo(CriteriaCondition.gte(NAME, "someValue"));
    }

    @Test
    public void shouldReturnLessThanConditionWhenLtIsCalled() {
        CriteriaCondition condition = attribute.lt("someValue");
        assertThat(condition).isEqualTo(CriteriaCondition.lt(NAME, "someValue"));
    }

    @Test
    public void shouldReturnLessThanOrEqualToConditionWhenLteIsCalled() {
        CriteriaCondition condition = attribute.lte("someValue");
        assertThat(condition).isEqualTo(CriteriaCondition.lte(NAME, "someValue"));
    }

    @Test
    public void shouldReturnBetweenConditionWhenBetweenIsCalled() {
        CriteriaCondition condition = attribute.between("valueA", "valueB");
        assertThat(condition).isEqualTo(CriteriaCondition.between(NAME, List.of("valueA", "valueB")));
    }

    @Test
    public void shouldReturnLikeConditionWhenLikeIsCalled() {
        CriteriaCondition condition = attribute.like("pattern");
        assertThat(condition).isEqualTo(CriteriaCondition.like(NAME, "pattern"));
    }
}