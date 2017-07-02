# Elasticsearch-extension

![Elasticsearch Project](https://github.com/JNOSQL/jnosql-site/blob/master/assets/img/logos/elastic.png)


Elasticsearch extension has implementations to use specific behavior that is beyond the API such as search Engine.


## ElasticsearchTemplate and ElasticsearchTemplateAsync

ElasticsearchTemplate and ElasticsearchTemplateAsync are a specialization of Document Template that allows using search engine on both synchronous and asynchronous.

```java
        template.sql("select * from Person where name = ?", "Ada");
          Consumer<Person> callBack = p -> {
        };
        template.live("select from Person where name = ?", callBack, "Ada");
        
         String query = "select * from Person where name = ?";
        Consumer<List<Person>> callBack = p -> {};

        templateAsync.sql(query, callBack, "Person");

```
