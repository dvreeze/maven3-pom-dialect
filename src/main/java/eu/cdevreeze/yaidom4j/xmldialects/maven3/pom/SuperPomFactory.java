/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.yaidom4j.xmldialects.maven3.pom;

import eu.cdevreeze.yaidom4j.dom.immutabledom.Document;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import org.xml.sax.InputSource;

import java.io.InputStream;

/**
 * Factory creating the super POM. Note that the super POM has no "target Maven coordinates".
 *
 * @author Chris de Vreeze
 */
public class SuperPomFactory {

    private SuperPomFactory() {
        // To prevent instance creation
    }

    public static ProjectElement createSuperPom() {
        return ProjectElement.from(parseSuperPomDocument().documentElement());
    }

    // TODO Parsing document only once

    private static Document parseSuperPomDocument() {
        String xmlClasspathResource = "/super-pom.xml";
        InputStream inputStream = SuperPomFactory.class.getResourceAsStream(xmlClasspathResource);
        return DocumentParsers.builder().removingInterElementWhitespace().build()
                .parse(new InputSource(inputStream));
    }
}
