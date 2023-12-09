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
package org.eclipse.jnosql.mapping.criteria;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.jnosql.mapping.document.DocumentEntityConverter;
import org.eclipse.jnosql.mapping.document.DocumentEventPersistManager;
import jakarta.inject.Inject;
import org.eclipse.jnosql.communication.document.DocumentManager;
import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.criteria.api.CriteriaDocumentTemplate;
import org.eclipse.jnosql.mapping.criteria.api.CriteriaDocumentTemplateProducer;
import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;

@ApplicationScoped
public class DefaultCriteriaDocumentTemplateProducer implements CriteriaDocumentTemplateProducer {

    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private EntitiesMetadata entities;

    @Inject
    private Converters converters;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Override
    public CriteriaDocumentTemplate get(DocumentManager dm) {
        return new DefaultCriteriaDocumentTemplate(
                dm,
                converter,
                entities,
                converters,
                persistManager
        );
    }

}
