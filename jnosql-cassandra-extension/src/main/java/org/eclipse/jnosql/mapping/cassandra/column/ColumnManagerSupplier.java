/*
 *  Copyright (c) 2022 Eclipse Contribuitor
 * All rights reserved. This program and the accompanying materials
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *    You may elect to redistribute this code under either of these licenses.
 */

package org.eclipse.jnosql.mapping.cassandra.column;

import jakarta.nosql.Settings;
import jakarta.nosql.mapping.MappingException;
import org.eclipse.jnosql.communication.cassandra.column.CassandraColumnManager;
import org.eclipse.jnosql.communication.cassandra.column.CassandraColumnManagerFactory;
import org.eclipse.jnosql.communication.cassandra.column.CassandraConfiguration;
import org.eclipse.jnosql.mapping.config.MicroProfileSettings;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.eclipse.jnosql.mapping.config.MappingConfigurations.DOCUMENT_DATABASE;

@ApplicationScoped
class ColumnManagerSupplier implements Supplier<CassandraColumnManager> {

    private static final Logger LOGGER = Logger.getLogger(ColumnManagerSupplier.class.getName());


    @Override
    @Produces
    @Typed(CassandraColumnManager.class)
    public CassandraColumnManager get() {
        Settings settings = MicroProfileSettings.INSTANCE;
        CassandraConfiguration configuration = new CassandraConfiguration();
        CassandraColumnManagerFactory factory = configuration.apply(settings);
        Optional<String> database = settings.get(DOCUMENT_DATABASE, String.class);
        String db = database.orElseThrow(() -> new MappingException("Please, inform the database filling up the property "
                + DOCUMENT_DATABASE));
        CassandraColumnManager manager = factory.apply(db);
        LOGGER.log(Level.FINEST, "Starting  a CassandraColumnManager instance using Eclipse MicroProfile Config," +
                " database name: " + db);
        return manager;
    }

    public void close(@Disposes CassandraColumnManager manager) {
        LOGGER.log(Level.FINEST, "Closing CassandraColumnManager resource, database name: " + manager.getName());
        manager.close();
    }

}
