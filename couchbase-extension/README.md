# Couchbase-extension

![Couchbase Project](https://github.com/JNOSQL/jnosql-site/blob/master/assets/img/logos/couchbase.png)


Couchbase extension has implementations to use specific behavior that is beyond the API such as N1QL.

## CouchbaseRepository

CouchbaseRepository is an extension of Repository that allows using N1QL annotation that executes N1QL.


```java
    interface PersonRepository extends CouchbaseRepository<Person, String> {

        @N1QL("select * from Person")
        List<Person> findAll();

        @N1QL("select * from Person where name = $name")
        List<Person> findByName(JsonObject params);
    }
```

## CouchbaseRepositoryAsync

CouchbaseRepositoryAsync is an extension of RepositoryAsync that allows using N1QL annotation that executes N1QL.


```java
    interface PersonAsyncRepository extends CouchbaseRepositoryAsync<Person, String> {

        Person findByName(String name);


        @N1QL("select * from Person where name= $name")
        void queryName(JsonObject params);

        @N1QL("select * from Person where name= $name")
        void queryName(JsonObject params, Consumer<List<Person>> callBack);
    }
```

