/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Transaction.Status;
import org.jnosql.artemis.graph.cdi.WeldJUnit4Runner;
import org.jnosql.artemis.graph.model.Book;
import org.jnosql.artemis.graph.model.BookTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.tinkerpop.gremlin.structure.Transaction.Status.COMMIT;
import static org.apache.tinkerpop.gremlin.structure.Transaction.Status.ROLLBACK;

@RunWith(WeldJUnit4Runner.class)
public class BookTemplateTest {

    @Inject
    private BookTemplate template;

    @Inject
    private Graph graph;


    @Test
    public void shouldSaveWithTransaction() {
        AtomicReference<Status> status = new AtomicReference<>();

        Book book = Book.builder().withName("The Book").build();
        Transaction transaction = graph.tx();
        transaction.addTransactionListener(status::set);
        template.insert(book);
        Assert.assertFalse(transaction.isOpen());
        Assert.assertEquals(COMMIT, status.get());
    }

    @Test
    public void shouldSaveWithRollback() {
        AtomicReference<Status> status = new AtomicReference<>();

        Book book = Book.builder().withName("The Book").build();
        Transaction transaction = graph.tx();
        transaction.addTransactionListener(status::set);
        try {
            template.insertException(book);
            assert false;
        }catch (Exception ex){

        }

        Assert.assertFalse(transaction.isOpen());
        Assert.assertEquals(ROLLBACK, status.get());
    }
}
