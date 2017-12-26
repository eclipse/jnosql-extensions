# Hazelcast-extension

![Hazelcast Project](https://jnosql.github.io/img/logos/hazelcast.svg)


Hazelcast extension has implementations to use specific behavior that is beyond the API such as Hazelcast Query.

## HazelcastRepository

HazelcastRepository is an extension of Repository that allows using Query annotation that executes Hazelcast query.

## Set dependency


```xml

  <dependency>
     <groupId>org.jnosql.artemis</groupId>
     <artifactId>hazelcast-extension</artifactId>
     <version>${jnosql.version}</version>
  </dependency>
```

## Make Hazelcast manager available to container

```java

public class HazelcastProducer {


    @Produces
    public HazelcastBucketManager getManager() {
        HazelcastBucketManager manager = ...;
        return manager;
    }
}


```

```java
interface PersonRepository extends HazelcastRepository<Person, String> {

        @Query("active")
        List<Person> findActive();

        @Query("name = :name AND age = :age")
        Set<Person> findByAgeAndInteger(@Param("name") String name, @Param("age") Integer age);
    }   
```


## HazelcastTemplate

HazelcastTemplate is a specialization of Key-value Template that allows using hazelcast query.

```java
      Collection<Person> people = template.query("active");
      Collection<Person> people2 = template.query("age = :age", singletonMap("age", 10));
      Collection<Person> people3 = template.query(Predicates.equal("name",  "Poliana"));
```
