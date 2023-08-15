/*
 *  Copyright (c) 2020 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.lite;


import jakarta.nosql.DatabaseType;
import jakarta.nosql.Param;
import jakarta.nosql.Query;
import jakarta.nosql.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public interface Person10RepositoryB extends Repository<Person10, Long> {

    Stream<Person10> findByName(String name);

    List<Person10> findByAge(Integer age);

    Optional<Person10> findByDocument(Integer age);

    Person10 findByDocument2(Integer age);

    @Query("select * from Person10 where name = @name")
    List<Person10> query(@Param("name") String name);
}
