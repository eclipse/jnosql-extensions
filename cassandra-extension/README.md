# Cassandra-extension

![Cassandra Project](https://github.com/JNOSQL/jnosql-site/blob/master/assets/img/logos/cassandra.png)


Cassandra extension has implementations to use specific behavior that is beyond the API such as Cassandra Query Language, consistency level.

## CassandraRepository

CassandraRepository is an extension of Repository that allows using CQL annotation that executes Cassandra Query Language and also Consistency Level.


```java
    interface PersonRepository extends CassandraRepository<Person, String> {

        Person findByName(String name, ConsistencyLevel level);

        void deleteByName(String name, ConsistencyLevel level);

        @CQL("select * from Person")
        List<Person> findAll();

        @CQL("select * from Person where name = ?")
        List<Person> findByName(String name);
    }
```

## CassandraRepositoryAsync

CassandraRepositoryAsync is an extension of RepositoryAsync that allows using CQL annotation that executes Cassandra Query Language and also Consistency Level.


```java
    interface PersonAsyncRepository extends CassandraRepositoryAsync<Person, String> {

        Person findByName(String name);

        Person findByName(String name, ConsistencyLevel level, Consumer<List<Person>> callBack);

        void deleteByName(String name, ConsistencyLevel level, Consumer<Void> callBack);

        @CQL("select * from Person where name= ?")
        void queryName(String name);

        @CQL("select * from Person where name= ?")
        void queryName(String name, Consumer<List<Person>> callBack);
    }
```

## @UDT

The UDT annotations is a mapping annotation that allows defining a field to be stored as User defined type in Cassandra.
