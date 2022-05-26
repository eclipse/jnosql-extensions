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
package jakarta.nosql.criteria;

/**
 * The <code>ExecutableQuery</code> interface defines functionality that is
 * specific to executable queries.
 *
 * @param <T> the type of the root entity
 * @param <R> the type of the query result
 */
public interface ExecutableQuery<T, R extends CriteriaQueryResult<T>> {
    
    /**
     * Retrieves the root type. 
     *
     * @return root type
     */
    Class<T> getType();
    
    /**
     * Retrieves the result. 
     *
     * @return result
     */
    R getResult();
    
}
