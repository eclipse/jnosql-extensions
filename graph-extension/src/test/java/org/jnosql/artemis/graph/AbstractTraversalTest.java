package org.jnosql.artemis.graph;

import org.jnosql.artemis.graph.model.Book;
import org.jnosql.artemis.graph.model.Person;
import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;

import static org.jnosql.artemis.graph.model.Person.builder;

public abstract class AbstractTraversalTest {

    static final String READS = "reads";
    @Inject
    protected GraphTemplate graphTemplate;


    protected Person otavio;
    protected Person poliana;
    protected Person paulo;

    protected Book shack;
    protected Book license;
    protected Book effectiveJava;

    protected EdgeEntity<Person, Book> reads;
    protected EdgeEntity<Person, Book> read1;
    protected EdgeEntity<Person, Book> reads2;

    @Before
    public void setUp() {

        otavio = graphTemplate.insert(builder().withAge(27)
                .withName("Otavio").build());
        poliana = graphTemplate.insert(builder().withAge(26)
                .withName("Poliana").build());
        paulo = graphTemplate.insert(builder().withAge(50)
                .withName("Paulo").build());

        shack = graphTemplate.insert(Book.builder().withAge(2007).withName("The Shack").build());
        license = graphTemplate.insert(Book.builder().withAge(2013).withName("Software License").build());
        effectiveJava = graphTemplate.insert(Book.builder().withAge(2001).withName("Effective Java").build());


        reads = graphTemplate.edge(otavio, READS, effectiveJava);
        read1 = graphTemplate.edge(poliana, READS, shack);
        reads2 = graphTemplate.edge(paulo, READS, license);
    }

    @After
    public void after() {
        graphTemplate.delete(otavio.getId());
        graphTemplate.delete(poliana.getId());
        graphTemplate.delete(paulo.getId());

        graphTemplate.delete(shack.getId());
        graphTemplate.delete(license.getId());
        graphTemplate.delete(effectiveJava.getId());

        reads.delete();
        read1.delete();
        reads2.delete();
    }
}
