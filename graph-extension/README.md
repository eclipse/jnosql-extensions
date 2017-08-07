# Graph-extension

![Graph Project](https://github.com/JNOSQL/jnosql-site/blob/master/assets/img/logos/tinkerpop.png)

JNoSQL has the support of the Graph database through the TinkerPop project. Apache TinkerPop™ is a graph computing framework for both graph databases (OLTP) and graph analytic systems (OLAP). The TinkerPop has support to more than twenty Graph databases.

## Graph Template

  This template has the duty to be a bridge between the entity model and Graph database.
  
 ```java 
  
GraphTemplate graphTemplate =//instance

 Person person = builder().withAge(27).withName("Otavio").build();
graphTemplate.insert(person);

person.setAge(29);
graphTemplate.update(person);
graphTemplate.delete(person.getId());

```

## Edge
 
   An Edge links two Vertex objects. Along with its Property objects, an Edge has both a Direction and a label. The Direction determines which Vertex is the tail Vertex (out Vertex) and which Vertex is the head Vertex (in Vertex). The Edge label determines the type of relationship that exists between the two vertices.

### EdgeEntity<OUT, IN>

The representation of Edge that links two Entity. Along with its Property objects, an Edge has both a Direction and a label.

```java
Person poliana = graphTemplate.insert(Person.builder().withName("Poliana").withAge(25).build());
Book shack = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
EdgeEntity<Person, Book> reads = graphTemplate.edge(poliana, "reads", shack);
reads.add("where", "Brazil");
int size = edge.size();
String label = edge.getLabel();
Person outbound = edge.getOutbound();
Book inbound = edge.getInbound();
```

#### Find an EdgeEntity


```java
Optional<EdgeEntity<Person, Book>> reads = graphTemplate.edge(edgeId);
```


## Repository

In addition to template class, Artemis has the Repository. This interface helps the Entity repository to save, update, delete and retrieve information.

To use Repository, just need to create a new interface that extends the Repository.

```java
 interface PersonRepository extends Repository<Person, String> {

}
```

The qualifier is mandatory to define the database type that will be used at the injection point moment.

```java
@Inject
@Database(DatabaseType.GRAPH)
private PersonRepository graphRepository;
```
