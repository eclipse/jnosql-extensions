# Cassandra-extension

![Cassandra Project](https://github.com/JNOSQL/jnosql-site/blob/master/assets/img/logos/cassandra.png)


Cassandra extension has implementations to use specific behavior that is beyond the API such as Cassandra Query Language, consistency level.


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
