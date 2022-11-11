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
 *   Alessandro Moscatelli
 */
package org.eclipse.jnosql.mapping.mongodb.criteria;

import org.eclipse.jnosql.mapping.mongodb.criteria.api.CriteriaQueryResult;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.Order;
import org.eclipse.jnosql.mapping.mongodb.criteria.api.SelectQuery;
import java.util.Arrays;
import java.util.List;

/**
 * Default implementation for {@link SelectQuery}
 *
 * @param <X> the type of the root entity
 * @param <R> the type of the select query result
 * @param <I> select query implementation
 */
public abstract class DefaultSelectQuery<X, R extends CriteriaQueryResult<X>, I extends DefaultSelectQuery<X, R, I>> extends AbstractRestrictedQuery<X, R, I> {

    private List<Order<X, ?>> sortings;
    private Integer maxResults;
    private Integer firstResult;
    private R result;

    public DefaultSelectQuery(Class<X> type) {
        super(type);
    }

    public I orderBy(Order<X, ?>... sortings) {
        this.sortings = Arrays.asList(sortings);
        return (I) this;
    }

    public List<Order<X, ?>> getOrderBy() {
        return this.sortings;
    }

    public I setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
        return (I) this;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public I setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
        return (I) this;
    }

    public Integer getFirstResult() {
        return firstResult;
    }
    
    protected void setResult(R result) {
        this.result = result;
    }

    public R getResult() {
        return this.result;
    }

}
