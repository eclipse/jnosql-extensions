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
 *   Maximillian Arruda
 */
package ee.jakarta.tck.data.example.extension;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;

//tag::applicationProcessor[]
public class MyApplicationArchiveProcessor implements ApplicationArchiveProcessor {
    
    //List of test classes that deploy application that you need to customize
//    List<String> testClasses;

    @Override
    public void process(Archive<?> archive, TestClass testClass) {
//        if(testClasses.contains(testClass.getClass().getCanonicalName())){
//            ((WebArchive) archive).addAsWebInfResource("my-custom-sun-web.xml", "sun-web.xml");
//        }
    }
}
//end::applicationProcessor[]
