package org.eclipse.jnosql.mapping.mongodb;

import org.eclipse.jnosql.mapping.test.CDIExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;


@CDIExtension
class MongoDBTemplateTest {

    @Inject
    private MongoDBTemplate template;

    @Test
    public void shouldInjectMongoDBTemplate() {
        Assertions.assertNotNull(template);
    }
}