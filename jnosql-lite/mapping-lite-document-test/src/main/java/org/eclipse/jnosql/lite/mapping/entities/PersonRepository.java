/*
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.lite.mapping.entities;

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.data.repository.Update;
import org.eclipse.jnosql.mapping.NoSQLRepository;

import java.util.List;

@Repository
public interface PersonRepository extends NoSQLRepository<Person, Long> {

    List<Person> findByName(String name);

    List<Person> findByNameOrderbyId(String name);

    Page<Person> findByName(String name, PageRequest<Person> pageRequest);

    @Query("select * from Person where name = @name")
    List<Person> query(@Param("name") String name);

    boolean existsByName(String name);

    long countByName(String name);

    void deleteByName(String name);
    @Insert
    Person insertPerson(Person person);

    @Insert
    void insertPersonVoid(Person person);

    @Insert
    int insertPersonInt(Person person);

    @Insert
    long insertPersonLong(Person person);

    @Insert
    Iterable<Person> insertIterable(Iterable<Person> people);

    @Insert
    void insertIterableVoid(Iterable<Person> people);

    @Insert
    int insertIterableInt(Iterable<Person> people);

    @Insert
    long insertIterableLong(Iterable<Person> people);

    @Insert
    Person[] insertArray(Person[] people);

    @Insert
    void insertArrayVoid(Person[] people);

    @Insert
    int insertArrayInt(Person[] people);

    @Insert
    long insertArrayLong(Person[] people);

    @Save
    Person savePerson(Person person);

    @Save
    void savePersonVoid(Person person);

    @Save
    int savePersonInt(Person person);

    @Save
    long savePersonLong(Person person);

    @Save
    Iterable<Person> saveIterable(Iterable<Person> people);

    @Save
    void saveIterableVoid(Iterable<Person> people);

    @Save
    int saveIterableInt(Iterable<Person> people);

    @Save
    long saveIterableLong(Iterable<Person> people);

    @Save
    Person[] saveArray(Person[] people);

    @Save
    void saveArrayVoid(Person[] people);

    @Save
    int saveArrayInt(Person[] people);

    @Save
    long saveArrayLong(Person[] people);

    @Update
    Person updatePerson(Person person);

    @Update
    void updatePersonVoid(Person person);

    @Update
    int updatePersonInt(Person person);

    @Update
    long updatePersonLong(Person person);

    @Update
    Iterable<Person> updateIterable(Iterable<Person> people);

    @Update
    void updateIterableVoid(Iterable<Person> people);

    @Update
    int updateIterableInt(Iterable<Person> people);

    @Update
    long updateIterableLong(Iterable<Person> people);

    @Update
    Person[] updateArray(Person[] people);

    @Update
    void updateArrayVoid(Person[] people);

    @Update
    int updateArrayInt(Person[] people);

    @Update
    long updateArrayLong(Person[] people);
    @Delete
    boolean deletePerson(Person person);

    @Delete
    void deletePersonVoid(Person person);

    @Delete
    int deletePersonInt(Person person);

    @Delete
    boolean deleteIterable(Iterable<Person> people);

    @Delete
    void deleteIterableVoid(Iterable<Person> people);

    @Delete
    int deleteIterableInt(Iterable<Person> people);

    @Delete
    long deleteIterableLong(Iterable<Person> people);

    @Delete
    boolean deleteArray(Person[] people);

    @Delete
    void deleteArrayVoid(Person[] people);

    @Delete
    int deleteArrayInt(Person[] people);

    @Delete
    long deleteArrayLong(Person[] people);

    @Delete
    long deletePersonLong(Person person);
}
