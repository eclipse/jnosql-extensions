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
package org.eclipse.jnosql.mapping.mongodb.criteria.api;

import org.eclipse.jnosql.mapping.mongodb.metamodel.api.Attribute;
import org.eclipse.jnosql.mapping.mongodb.metamodel.api.ComparableAttribute;
import org.eclipse.jnosql.mapping.mongodb.metamodel.api.EntityAttribute;
import org.eclipse.jnosql.mapping.mongodb.metamodel.api.NumberAttribute;
import org.eclipse.jnosql.mapping.mongodb.metamodel.api.StringAttribute;
import org.eclipse.jnosql.mapping.mongodb.metamodel.api.ValueAttribute;

/**
 * The Entity type representing a path from the root type in a {@link CriteriaQuery}
 *
 * @param <X> the entity type referenced by the root
 * @param <Y> the destination type
 */
public interface Path<X, Y> {
    
    /**
     * Returns the parent path
     *
     * @return parent path
     */
    Path<X, ?> getParent();
    
    /**
     * Returns the attribute that binds parent {@link Path} to this
     *
     * @return parent path
     */
    Attribute<?, Y> getAttribute();


    /**
     * Create a path corresponding to the referenced entity attribute
     *
     * @param <Z> the type of the entity attribute
     * @param attribute entity attribute
     * @return path corresponding to the entity attribute
     */
    <Z> Path<X, Z> get(EntityAttribute<Y, Z> attribute);
    
    /**
     * Create an expression corresponding to the referenced single-valued
     * attribute
     *
     * @param <Z> the type of the attribute
     * @param attribute single-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    <Z> Expression<X, Y, Z> get(ValueAttribute<Y, Z> attribute);

    /**
     * Create an expression corresponding to the referenced single-valued string
     * attribute
     *
     * @param attribute single-valued string attribute
     * @return string expression corresponding to the referenced string
     * attribute
     */
    StringExpression<X, Y> get(StringAttribute<Y> attribute);

    /**
     * Create an expression corresponding to the referenced single-valued
     * comparable attribute
     *
     * @param <Z> the type of the comparable attribute
     * @param attribute single-valued comparable attribute
     * @return comparable expression corresponding to the referenced comparable
     * attribute
     */
    <Z extends Comparable> ComparableExpression<X, Y, Z> get(ComparableAttribute<Y, Z> attribute);

    /**
     * Create an expression corresponding to the referenced single-valued
     * number attribute
     *
     * @param <Z> the type of the number attribute
     * @param attribute single-valued number attribute
     * @return comparable expression corresponding to the referenced comparable
     * attribute
     */
    <Z extends Number & Comparable> NumberExpression<X, Y, Z> get(NumberAttribute<Y, Z> attribute);

}
