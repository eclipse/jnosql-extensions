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
 *   Alessandro Moscatelli
 */
package org.eclipse.jnosql.mapping.mongodb.criteria.impl;

import org.eclipse.jnosql.mapping.mongodb.criteria.api.Predicate;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.RestrictedQuery;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.CriteriaQueryResult;
import java.util.Arrays;
import java.util.Collection;

/**
 * Abstract class to extend for {@link RestrictedQuery} implementations
 *
 * @param <T> the type of the root entity
 * @param <R> the type of the query result
 * @param <Q> the type of the restricted query
 */
public abstract class AbstractRestrictedQuery<T, R extends CriteriaQueryResult<T>, Q extends AbstractRestrictedQuery<T, R, Q>> extends DefaultCriteriaQuery<T> {

    private Collection<Predicate<T>> restrictions;

    public AbstractRestrictedQuery(Class<T> type) {
        super(type);
    }

    public Q where(Predicate<T>... restrictions) {
        this.restrictions = Arrays.asList(restrictions);
        return (Q) this;
    }

    public Collection<Predicate<T>> getRestrictions() {
        return restrictions;
    }

}
