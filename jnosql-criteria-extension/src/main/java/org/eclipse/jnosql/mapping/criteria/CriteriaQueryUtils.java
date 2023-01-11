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
package org.eclipse.jnosql.mapping.criteria;

import jakarta.nosql.Sort;
import org.eclipse.jnosql.mapping.criteria.api.BinaryPredicate;
import org.eclipse.jnosql.mapping.criteria.api.CompositionPredicate;
import org.eclipse.jnosql.mapping.criteria.api.DisjunctionPredicate;
import org.eclipse.jnosql.mapping.criteria.api.Expression;
import org.eclipse.jnosql.mapping.criteria.api.ExpressionQuery;
import org.eclipse.jnosql.mapping.criteria.api.NegationPredicate;
import org.eclipse.jnosql.mapping.criteria.api.Order;
import org.eclipse.jnosql.mapping.criteria.api.Path;
import org.eclipse.jnosql.mapping.criteria.api.Predicate;
import org.eclipse.jnosql.mapping.criteria.api.RangePredicate;
import org.eclipse.jnosql.mapping.criteria.api.Root;
import jakarta.nosql.document.DocumentCondition;
import jakarta.nosql.document.DocumentQuery;
import org.eclipse.jnosql.mapping.criteria.api.SelectQuery;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.eclipse.jnosql.mapping.util.StringUtils;

/**
 * Utility to convert {@link Predicate}s in {@link DocumentCondition}s
 */
public class CriteriaQueryUtils {

    private CriteriaQueryUtils() {
    }

    public static String join(String... values) {
        return String.join(".", values);
    }

    public static String unfold(Path path) {
        Path tmp = path;
        Deque<String> attributes = new ArrayDeque();
        while (Objects.nonNull(tmp) && !(tmp instanceof Root)) {
            attributes.add(
                    tmp.getAttribute().getName()
            );
            tmp = tmp.getParent();
        }
        return join(
                attributes.stream().filter(
                        value -> StringUtils.isNotBlank(
                                value.trim()
                        )
                ).toArray(
                        String[]::new
                )
        );
    }

    public static String unfold(Expression expression) {
        return join(
                Arrays.asList(
                        unfold(
                                expression.getPath()
                        ),
                        expression.getAttribute().getName()
                ).stream().filter(
                        value -> !Objects.equals(
                                0,
                                value.trim().length()
                        )
                ).toArray(
                        String[]::new
                )
        );
    }

    public static DocumentCondition computeCondition(Predicate predicate) {
        DocumentCondition result = null;
        if (predicate instanceof CompositionPredicate) {
            Collection<Predicate> restrictions = CompositionPredicate.class.cast(predicate).getPredicates();
            Function<DocumentCondition[], DocumentCondition> function = predicate instanceof DisjunctionPredicate
                    ? DocumentCondition::or
                    : DocumentCondition::and;
            result = function.apply(
                    restrictions.stream().map(
                            CriteriaQueryUtils::computeCondition
                    ).toArray(
                            DocumentCondition[]::new
                    )
            );
        } else if (predicate instanceof NegationPredicate) {
            result = computeCondition(
                    NegationPredicate.class.cast(predicate).getPredicate()
            ).negate();
        } else if (predicate instanceof BinaryPredicate) {
            BinaryPredicate cast = BinaryPredicate.class.cast(predicate);
            String lhs = unfold(
                    cast.getLeft()
            );
            Object rhs = cast.getRight();
            if (rhs instanceof Expression) {
                throw new UnsupportedOperationException("Not supported yet.");
            } else {
                BiFunction<String, Object, DocumentCondition> bifunction;
                switch (cast.getOperator()) {
                    case EQUAL:
                        bifunction = DocumentCondition::eq;
                        break;
                    case GREATER_THAN:
                        bifunction = DocumentCondition::gt;
                        break;
                    case GREATER_THAN_OR_EQUAL:
                        bifunction = DocumentCondition::gte;
                        break;
                    case LESS_THAN:
                        bifunction = DocumentCondition::lt;
                        break;
                    case LESS_THAN_OR_EQUAL:
                        bifunction = DocumentCondition::lte;
                        break;
                    case IN:
                        bifunction = DocumentCondition::in;
                        break;
                    case LIKE:
                        bifunction = DocumentCondition::like;
                        break;
                    default:
                        throw new UnsupportedOperationException("Not supported yet.");
                }
                result = bifunction.apply(lhs, rhs);
            }
        } else if (predicate instanceof RangePredicate) {
            RangePredicate cast = RangePredicate.class.cast(predicate);
            String lhs = unfold(
                    cast.getLeft()
            );
            Object from = cast.getFrom();
            Object to = cast.getTo();
            if (from instanceof Expression || to instanceof Expression) {
                throw new UnsupportedOperationException("Not supported yet.");
            } else {
                BiFunction<String, Object, DocumentCondition> bifunction;
                switch (cast.getOperator()) {
                    case EXCLUSIVE_BETWEEN:
                    case INCLUSIVE_BETWEEN:
                        bifunction = DocumentCondition::between;
                        break;
                    default:
                        throw new UnsupportedOperationException("Not supported yet.");
                }
                result = bifunction.apply(lhs, Arrays.asList(from, to));
            }
        }
        if (Objects.isNull(result)) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        return result;
    }

    public static <X> DocumentQuery convert(SelectQuery<X, ?, ?, ?> selectQuery) {
        requireNonNull(selectQuery, "query is required");

        DocumentQuery.DocumentQueryBuilder builder;

        if (selectQuery instanceof ExpressionQuery) {
            ExpressionQuery<X> expressionQuery = ExpressionQuery.class.cast(selectQuery);
            builder = DocumentQuery.builder(
                    expressionQuery.getExpressions().stream().map(
                            CriteriaQueryUtils::unfold
                    ).toArray(
                            String[]::new
                    )
            );
        } else {
            builder = DocumentQuery.builder();
        }

        DocumentQuery.DocumentQueryBuilder where = builder.from(
                selectQuery.getType().getSimpleName()
        ).where(
                DocumentCondition.and(
                        Optional.ofNullable(
                                selectQuery.getRestrictions()
                        ).orElse(
                                Collections.<Predicate<X>>emptyList()
                        ).stream().map(
                                CriteriaQueryUtils::computeCondition
                        ).toArray(
                                DocumentCondition[]::new
                        )
                )
        ).sort(
                Optional.ofNullable(
                        selectQuery.getOrderBy()
                ).orElse(
                        Collections.<Order<X, ?>>emptyList()
                ).stream().map(
                        orderBy -> {
                            String unfold = unfold(orderBy.getExpression());
                            return orderBy.isAscending() ? Sort.asc(unfold) : Sort.desc(unfold);
                        }
                ).toArray(
                        Sort[]::new
                )
        );

        DocumentQuery.DocumentQueryBuilder limit = Optional.ofNullable(
                selectQuery.getMaxResults()
        ).map(
                where::limit
        ).orElse(where);

        DocumentQuery.DocumentQueryBuilder skip = Optional.ofNullable(
                selectQuery.getFirstResult()
        ).map(
                limit::skip
        ).orElse(limit);

        return skip.build();
    }

}
