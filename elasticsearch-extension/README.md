# Elasticsearch-extension

![Elasticsearch Project](https://github.com/JNOSQL/jnosql-site/blob/master/assets/img/logos/elastic.png)


Elasticsearch extension has implementations to use specific behavior that is beyond the API such as search Engine.


## ElasticsearchTemplate and ElasticsearchTemplateAsync

ElasticsearchTemplate and ElasticsearchTemplateAsync are a specialization of Document Template that allows using search engine on both synchronous and asynchronous.

```java
        QueryBuilder queryBuilder = boolQuery().filter(termQuery("name", "Ada"));
        List<Person> people = template.search(queryBuilder, "Person");
        
        QueryBuilder queryBuilder = boolQuery().filter(termQuery("name", "Ada"));
        Consumer<List<Person>> callBack = p -> {};

        templateAsync.search(queryBuilder, callBack, "Person");

```
