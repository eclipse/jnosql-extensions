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


## OrientDBTemplate and OrientDBTemplateAsync

OrientDBTemplate and OrientDBTemplateAsync are a specialization of Document Template that allows using SQL query and live query on both synchronous and asynchronous.

```java
        template.sql("select * from Person where name = ?", "Ada");
          Consumer<Person> callBack = p -> {
        };
        template.live("select from Person where name = ?", callBack, "Ada");
        
         String query = "select * from Person where name = ?";
        Consumer<List<Person>> callBack = p -> {};

        templateAsync.sql(query, callBack, "Person");

```
