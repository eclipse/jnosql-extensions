# Couchbase-extension

![Couchbase Project](https://jnosql.github.io/img/logos/couchbase.svg)


Couchbase extension has implementations to use specific behavior that is beyond the API such as N1QL.

## CouchbaseRepository

CouchbaseRepository is an extension of Repository that allows using N1QL annotation that executes N1QL.


```java
    interface PersonRepository extends CouchbaseRepository<Person, String> {

        @N1QL("select * from Person")
        List<Person> findAll();

        @N1QL("select * from Person where name = $name")
        List<Person> findByName(@Param("name") String name);
    }
```

## CouchbaseRepositoryAsync

CouchbaseRepositoryAsync is an extension of RepositoryAsync that allows using N1QL annotation that executes N1QL.


```java
    interface PersonAsyncRepository extends CouchbaseRepositoryAsync<Person, String> {

        Person findByName(String name);


        @N1QL("select * from Person where name= $name")
        void queryName(@Param("name") String name);

        @N1QL("select * from Person where name= $name")
        void queryName(@Param("name") String name, Consumer<List<Person>> callBack);
    }
```


## CouchbaseTemplate and CouchbaseTemplateAsync

CouchbaseTemplate is a specialization of Document Template that allows using N1QL both synchronous and asynchronous.

```java
        template.n1qlQuery("select * from Person where name = $name", params);

        String query = "select * from Person where name = ?";
        Consumer<List<Person>> callBack = p -> {
        };
        JsonObject params = JsonObject.create().put("name", "Ada");
        templateAsync.n1qlQuery(query, params, callBack);

```
