# OriendtDB-extension

![OriendtDB Project](https://github.com/JNOSQL/jnosql-site/blob/master/assets/img/logos/orientdb.png)


OrientDB extension has implementations to use specific behavior that is beyond the API such as SQL.

## OrientDBCrudRepository

OrientDBCrudRepository is an extension of Repository that allows using SQL annotation that executes SQL Query.


```java
    interface PersonRepository extends OrientDBCrudRepository<Person, String> {

        @SQL("select * from Person")
        List<Person> findAll();

        @SQL("select * from Person where name = ?")
        List<Person> findByName(String name);
    }
```

## OrientDBCrudRepositoryAsync

OrientDBCrudRepositoryAsync is an extension of RepositoryAsync that allows using N1QL annotation that executes SQL Query.


```java
    interface PersonAsyncRepository extends OrientDBCrudRepositoryAsync<Person, String> {

        Person findByName(String name);


        @SQL("select * from Person where name= ?")
        void queryName(String name);

        @SQL("select * from Person where name= ?")
        void queryName(String name, Consumer<List<Person>> callBack);
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
