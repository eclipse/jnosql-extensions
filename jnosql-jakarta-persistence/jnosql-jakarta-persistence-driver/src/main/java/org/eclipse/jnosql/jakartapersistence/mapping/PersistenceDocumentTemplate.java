/*
 *  Copyright (c) 2024 Contributors to the Eclipse Foundation
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
 *   Ondro Mihalyi
 */
package org.eclipse.jnosql.jakartapersistence.mapping;

import static org.eclipse.jnosql.communication.Condition.LESSER_EQUALS_THAN;
import static org.eclipse.jnosql.communication.Condition.LESSER_THAN;

import org.eclipse.jnosql.jakartapersistence.communication.PersistenceDatabaseManager;
import jakarta.annotation.Priority;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.nosql.QueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.semistructured.CriteriaCondition;
import org.eclipse.jnosql.communication.semistructured.DeleteQuery;
import org.eclipse.jnosql.communication.semistructured.Element;
import org.eclipse.jnosql.communication.semistructured.SelectQuery;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.PreparedStatement;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@Default
@ApplicationScoped
@Database(DatabaseType.DOCUMENT)
public class PersistenceDocumentTemplate implements DocumentTemplate {

    private final PersistenceDatabaseManager manager;

    @Inject
    PersistenceDocumentTemplate(PersistenceDatabaseManager manager) {
        this.manager = manager;
    }

    PersistenceDocumentTemplate() {
        this(null);
    }

    @Override
    public long count(String entity) {
        EntityType<?> entityType = manager.findEntityType(entity);
        return count(entityType.getJavaType());
    }

    @Override
    public <T> long count(Class<T> type) {
        TypedQuery<Long> query = buildQuery(type, Long.class, ctx -> ctx.query.select(ctx.builder.count(ctx.root)));
        return query.getSingleResult();
    }

    @Override
    public <T> Stream<T> findAll(Class<T> type) {
        TypedQuery<T> query = buildQuery(type, type, ctx -> ctx.query.select((Root<T>) ctx.root));
        return query.getResultStream();
    }

    record QuaryContext<FROM, RESULT>(CriteriaQuery<RESULT> query, Root<FROM> root, CriteriaBuilder builder) {

    }

    private <FROM, RESULT> TypedQuery<RESULT> buildQuery(Class<FROM> fromType, Class<RESULT> resultType,
            Function<QuaryContext<FROM, RESULT>, CriteriaQuery<RESULT>> queryModifier) {
        EntityManager em = entityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RESULT> criteriaQuery = criteriaBuilder.createQuery(resultType);
        Root<FROM> from = criteriaQuery.from(fromType);
        criteriaQuery = queryModifier.apply(new QuaryContext(criteriaQuery, from, criteriaBuilder));
        return em.createQuery(criteriaQuery);
    }

    private Query buildQuery(String query) {
        EntityManager em = entityManager();
        return em.createQuery(query);
    }

    private EntityManager entityManager() {
        return manager.getEntityManager();
    }

    @Override
    public <T> Stream<T> query(String query) {
        return buildQuery(query).getResultStream();
    }

    @Override
    public <T> Stream<T> query(String query, String entity) {
        return query(query);
    }

    @Override
    public <T> Optional<T> singleResult(String query) {
        return Optional.ofNullable((T) buildQuery(query).getSingleResultOrNull());
    }

    @Override
    public <T> Optional<T> singleResult(String query, String entity) {
        return singleResult(query);
    }

    @Override
    public <T, K> Optional<T> find(Class<T> type, K k) {
        return Optional.ofNullable(entityManager().find(type, k));
    }

    @Override
    public <T> T insert(T t) {
        entityManager().persist(t);
        return t;
    }

    @Override
    public <T> T update(T t) {
        return entityManager().merge(t);
    }

    @Override
    public PreparedStatement prepare(String query, String entity) {
        return prepare(query);
    }

    @Override
    public PreparedStatement prepare(String queryString) {
        return new PreparedStatement() {

            private Map<String, Object> parameters = new HashMap<>();

            private void applyParameters(Query query) {
                parameters.forEach((name, value) -> query.setParameter(name, value));
            }

            @Override
            public PreparedStatement bind(String name, Object value) {
                parameters.put(name, value);
                return this;
            }

            @Override
            public <T> Stream<T> result() {
                Query query = buildQuery(queryString);
                applyParameters(query);
                return query.getResultStream();
            }

            @Override
            public <T> Optional<T> singleResult() {
                Query query = buildQuery(queryString);
                applyParameters(query);
                return Optional.ofNullable((T) query.getSingleResultOrNull());
            }

            @Override
            public long count() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isCount() {
                return false;
            }
        };
    }

    @Override
    public void delete(DeleteQuery query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> Stream<T> select(SelectQuery selectQuery) {
        final String entityName = selectQuery.name();
        final EntityType<T> entityType = manager.findEntityType(entityName);
        if (selectQuery.condition().isEmpty()) {
            return findAll(entityType.getJavaType());
        } else {
            final CriteriaCondition criteria = selectQuery.condition().get();
            TypedQuery<T> query = buildQuery(entityType.getJavaType(), entityType.getJavaType(), ctx -> {
                CriteriaQuery<T> q = ctx.query.select(ctx.root);
                q = q.where(parseCriteria(criteria, ctx));
                return q;
            });
            return query.getResultStream();
        }
    }

    @Override
    public <T> Optional<T> singleResult(SelectQuery selectQuery) {
        final String entityName = selectQuery.name();
        final EntityType<T> entityType = manager.findEntityType(entityName);
        final Class<T> type = entityType.getJavaType();
        if (selectQuery.condition().isEmpty()) {
            TypedQuery<T> query = buildQuery(type, type, ctx -> ctx.query.select((Root<T>) ctx.root));
            return Optional.ofNullable(query.getSingleResultOrNull());
        } else {
            final CriteriaCondition criteria = selectQuery.condition().get();
            TypedQuery<T> query = buildQuery(type, type, ctx -> {
                CriteriaQuery<T> q = ctx.query.select(ctx.root);
                q = q.where(parseCriteria(criteria, ctx));
                return q;
            });
            return Optional.ofNullable(query.getSingleResultOrNull());
        }
    }

    @Override
    public long count(SelectQuery selectQuery) {
        final String entityName = selectQuery.name();
        if (selectQuery.condition().isEmpty()) {
            return count(entityName);
        } else {
            final EntityType<?> entityType = manager.findEntityType(entityName);
            final CriteriaCondition criteria = selectQuery.condition().get();
            TypedQuery<Long> query = buildQuery(entityType.getJavaType(), Long.class, ctx -> {
                CriteriaQuery<Long> q = ctx.query.select(ctx.builder.count(ctx.root));
                q = q.where(parseCriteria(criteria, ctx));
                return q;
            });
            return query.getSingleResult();
        }
    }

    record ComparableContext(Path<Comparable> field, Comparable fieldValue) {

        public static <FROM> ComparableContext from(Root<FROM> root, CriteriaCondition criteria) {
            Element element = (Element) criteria.element();
            Path<Comparable> field = root.get(getName(element));
            Comparable fieldValue = element.value().get(Comparable.class);
            return new ComparableContext(field, fieldValue);
        }
    }

    record BiComparableContext(Path<Comparable> field, Comparable fieldValue1, Comparable fieldValue2) {

        public static <FROM> BiComparableContext from(Root<FROM> root, CriteriaCondition criteria) {
            Element element = (Element) criteria.element();
            final Path<Comparable> field = root.get(getName(element));
            Iterator<?> iterator = elementCollection(criteria).iterator();
            final Comparable fieldValue1 = ((Value) iterator.next()).get(Comparable.class);
            final Comparable fieldValue2 = ((Value) iterator.next()).get(Comparable.class);
            return new BiComparableContext(field, fieldValue1, fieldValue2);
        }

    }

    record MultiValueContext(Path<?> field, Collection<?> fieldValues) {

        public static <FROM> MultiValueContext from(Root<FROM> root, CriteriaCondition criteria) {
            Element element = (Element) criteria.element();
            Path<Comparable> field = root.get(getName(element));
            return new MultiValueContext(field, elementCollection(criteria));
        }
    }

    private static String getName(Element element) {
        String name = element.name();
        // NoSQL DBs translate id field into "_id" but we don't want it
        return name.equals("_id") ? "id" : name;
    }

    private <FROM, RESULT> Predicate parseCriteria(Object value, QuaryContext<FROM, RESULT> ctx) {
        if (value instanceof CriteriaCondition criteria) {
            return switch (criteria.condition()) {
                case NOT ->
                    ctx.builder().not(parseCriteria(criteria.element(), ctx));
                case EQUALS -> {
                    Element element = (Element) criteria.element();
                    if (element.value().isNull()) {
                        yield ctx.builder().isNull(ctx.root().get(getName(element)));
                    } else {
                        yield ctx.builder().equal(ctx.root().get(getName(element)), element.value().get());
                    }
                }
                case AND -> {
                    Iterator<?> iterator = elementCollection(criteria).iterator();
                    yield ctx.builder().and(parseCriteria(iterator.next(), ctx), parseCriteria(iterator.next(), ctx));
                }
                case LESSER_THAN -> {
                    ComparableContext comparableContext = ComparableContext.from(ctx.root(), criteria);
                    yield ctx.builder().lessThan(comparableContext.field(), comparableContext.fieldValue());
                }
                case LESSER_EQUALS_THAN -> {
                    ComparableContext comparableContext = ComparableContext.from(ctx.root(), criteria);
                    yield ctx.builder().lessThanOrEqualTo(comparableContext.field(), comparableContext.fieldValue());
                }
                case GREATER_THAN -> {
                    ComparableContext comparableContext = ComparableContext.from(ctx.root(), criteria);
                    yield ctx.builder().greaterThan(comparableContext.field(), comparableContext.fieldValue());
                }
                case GREATER_EQUALS_THAN -> {
                    ComparableContext comparableContext = ComparableContext.from(ctx.root(), criteria);
                    yield ctx.builder().greaterThanOrEqualTo(comparableContext.field(), comparableContext.fieldValue());
                }
                case BETWEEN -> {
                    BiComparableContext comparableContext = BiComparableContext.from(ctx.root(), criteria);
                    yield ctx.builder().between(comparableContext.field(), comparableContext.fieldValue1(), comparableContext.fieldValue2());
                }
                case IN -> {
                    MultiValueContext valueContext = MultiValueContext.from(ctx.root(), criteria);
                    CriteriaBuilder.In<Object> inExpr = ctx.builder().in(valueContext.field());
                    valueContext.fieldValues().forEach(v -> inExpr.value(v));
                    yield inExpr;
                }

                default ->
                    throw new UnsupportedOperationException("Not supported yet.");
            };
        } else if (value instanceof Element element) {
            return parseCriteria(element.value().get(), ctx);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static Collection<?> elementCollection(CriteriaCondition criteria) {
        Element element = (Element) criteria.element();
        return (Collection<?>) element.value().get();
    }

    @Override
    public boolean exists(SelectQuery query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void deleteAll(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> CursoredPage<T> selectCursor(SelectQuery query, PageRequest pageRequest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T insert(T t, Duration drtn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> itrbl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> itrbl, Duration drtn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> itrbl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T, K> void delete(Class<T> type, K k) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> QueryMapper.MapperFrom select(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> QueryMapper.MapperDeleteFrom delete(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
