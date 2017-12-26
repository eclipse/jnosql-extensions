# Couchbase-extension

![Couchbase Project](https://jnosql.github.io/img/logos/couchbase.svg)


Couchbase extension has implementations to use specific behavior that is beyond the API such as N1QL.


## Set dependency


```xml

  <dependency>
     <groupId>org.jnosql.artemis</groupId>
     <artifactId>couchbase-extension</artifactId>
     <version>${jnosql.version}</version>
  </dependency>
```

## Make Couchbase manager available to container

```java

public class CouchbaseProducer {


    @Produces
    public CouchbaseDocumentCollectionManager getManager() {
        CouchbaseDocumentCollectionManager manager = ...;
        return manager;
    }

    @Produces
    public CouchbaseDocumentCollectionManagerAsync getManagerAsync() {
        CouchbaseDocumentCollectionManagerAsync managerAsync = ...;
        return managerAsync;
    }
}


```


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

        @N1QL("select * from Person where name= $name")
        void queryName(@Param("name") String name);

        @N1QL("select * from Person where name= $name")
        void queryName(@Param("name") String name, Consumer<List<Person>> callBack);
    }
```


## CouchbaseTemplate and CouchbaseTemplateAsync

CouchbaseTemplate is a specialization of Document Template that allows using N1QL both synchronous and asynchronous.

```java
        List<Person> people = template.n1qlQuery("select * from Person where name = $name", params);

        MatchQuery match = SearchQuery.match("Ada");
        SearchQuery query = new SearchQuery("index-person", match);
        List<Person> resultl = template.search(query);

        String query = "select * from Person where name = ?";
        Consumer<List<Person>> callBack = p -> {
        };
        JsonObject params = JsonObject.create().put("name", "Ada");
        templateAsync.n1qlQuery(query, params, callBack);

```
