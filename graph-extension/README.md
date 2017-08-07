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
Person person = graphTemplate.insert(Person.builder().withName("Poliana").withAge(25).build());
Book book = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
EdgeEntity<Person, Book> edge = graphTemplate.edge(person, "reads", book);
```
