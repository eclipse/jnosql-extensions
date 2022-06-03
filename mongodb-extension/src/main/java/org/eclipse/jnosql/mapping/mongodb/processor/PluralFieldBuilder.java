/*
 *  Copyright (c) 2022 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.mongodb.processor;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.eclipse.jnosql.mapping.mongodb.metamodel.api.PluralAttribute;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.eclipse.jnosql.mapping.mongodb.metamodel.DefaultPluralAttribute;

/**
 * Field builder for plural attributes.
 */
public class PluralFieldBuilder extends AbstractSimpleFieldBuilder<PluralAttribute, DefaultPluralAttribute> {

    @Override
    public void buildField(JCodeModel codeModel, JDefinedClass jClass, TypeElement typeElement, Element element) {
        super.buildField(codeModel, jClass, typeElement, element, PluralAttribute.class, DefaultPluralAttribute.class);
    }

}
