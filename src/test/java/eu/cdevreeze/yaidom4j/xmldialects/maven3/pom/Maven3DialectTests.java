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

import module java.base;
import module eu.cdevreeze.yaidom4j;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests querying Maven 3 POM files.
 *
 * @author Chris de Vreeze
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Maven3DialectTests {

    private Document doc;

    @BeforeAll
    void parseDocuments() {
        doc = parseDocument("/sample-pom.xml");
    }

    private Document parseDocument(String xmlClasspathResource) {
        InputStream inputStream = Maven3DialectTests.class.getResourceAsStream(xmlClasspathResource);
        return DocumentParsers.builder().removingInterElementWhitespace().build()
                .parse(new InputSource(inputStream));
    }

    @Test
    void testQueryDependencies() {
        PomProperties extraProperties = new PomProperties(ImmutableMap.of("revision", "0.0.1-SNAPSHOT"));

        ProjectElement projectElement = ProjectElement.from(doc.documentElement());
        // TODO Use super POM
        EffectivePom effectivePom = EffectivePom.from(projectElement);

        Dependency expectedDependency =
                new Dependency("kms-api-examples", "kms-api-examples", "0.0.1-SNAPSHOT");
        assertEquals(expectedDependency, effectivePom.artifactAsDependency(extraProperties));

        List<Dependency> dependencies = effectivePom.dependencies(extraProperties);

        List<Dependency> expectedDependencies = List.of(
                new Dependency("org.apache.httpcomponents", "httpclient", "4.3.2"),
                new Dependency("org.apache.httpcomponents", "httpclient-cache", "4.3.2"),
                new Dependency("org.apache.httpcomponents", "httpmime", "4.3.2"),
                new Dependency("com.fasterxml.jackson.core", "jackson-core", "2.4.0"),
                new Dependency("com.fasterxml.jackson.core", "jackson-databind", "2.4.0")
        );
        assertEquals(expectedDependencies, dependencies);
    }
}
