/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
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
 *   Alessandro Moscatelli
 */
package org.eclipse.jnosql.mapping.metamodel.processor;

import org.eclipse.jnosql.mapping.metamodel.api.NumberAttribute;
import org.eclipse.jnosql.mapping.metamodel.api.StringAttribute;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MetamodelTest {

    @Test
    public void shouldGenerateMetamodel() {

        assertTrue(
                Person_.name instanceof StringAttribute
        );
        assertTrue(
                Person_.age instanceof NumberAttribute
        );
        assertTrue(
                Music_.id instanceof StringAttribute
        );
        assertTrue(
                Music_.name instanceof StringAttribute
        );
        assertTrue(
                Music_.year instanceof NumberAttribute
        );

    }

}
