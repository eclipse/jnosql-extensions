# Graph-extension

![Graph Project](https://github.com/JNOSQL/jnosql-site/blob/master/assets/img/logos/tinkerpop.png)

JNoSQL has the support of the Graph database through the TinkerPop project. Apache TinkerPop™ is a graph computing framework for both graph databases (OLTP) and graph analytic systems (OLAP). The TinkerPop has support to more than twenty Graph databases.

## Graph Template

  This template has the duty to be a bridge between the entity model and Graph database.
  
 ```java 
  
GraphTemplate graphTemplate =//instance

 Person person = builder().withAge(27)
                .withName("Otavio").build();
graphTemplate.insert(person);

person.setAge(29);
graphTemplate.update(person);
graphTemplate.delete(person.getId());

```
