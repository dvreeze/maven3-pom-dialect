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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.yaidom4j.core.ElementNavigationPath;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.AncestryAwareDocument;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.AncestryAwareNodes;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.AncestryAwareElementPredicates.hasLocalName;
import static eu.cdevreeze.yaidom4j.xmldialects.maven3.pom.AnyPomElement.MAVEN_POM_NS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests querying Maven 3 POM files.
 *
 * @author Chris de Vreeze
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Maven3DialectTests {

    private AncestryAwareDocument doc1;
    private AncestryAwareDocument doc2;

    @BeforeAll
    void parseDocuments() {
        doc1 = parseDocument("/sample-pom.xml");
        doc2 = parseDocument("/large-sample-pom.xml");
    }

    private AncestryAwareDocument parseDocument(String xmlClasspathResource) {
        InputStream inputStream = Maven3DialectTests.class.getResourceAsStream(xmlClasspathResource);
        var doc = DocumentParsers.builder().removingInterElementWhitespace().build()
                .parse(new InputSource(inputStream));
        return AncestryAwareDocument.from(doc);
    }

    @Test
    void testCountElementNames() {
        ProjectElement projectElement = ProjectElement.from(doc1.documentElement());

        assertEquals(1, projectElement.descendantElementOrSelfStream(ProjectElement.class).count());

        assertTrue(projectElement.artifactIdElementOption().isPresent());
        assertTrue(projectElement.groupIdElementOption().isPresent());
        assertTrue(projectElement.versionElementOption().isPresent());

        assertEquals(5, projectElement.descendantElementOrSelfStream(DependencyElement.class).count());
        assertEquals(
                5,
                projectElement.descendantElementOrSelfStream(DependencyElement.class)
                        .flatMap(e -> e.childElementStream(GroupIdElement.class))
                        .count()
        );
        assertEquals(
                5,
                projectElement.descendantElementOrSelfStream(DependencyElement.class)
                        .flatMap(e -> e.childElementStream(ArtifactIdElement.class))
                        .count()
        );
        assertEquals(
                5,
                projectElement.descendantElementOrSelfStream(DependencyElement.class)
                        .flatMap(e -> e.childElementStream(VersionElement.class))
                        .count()
        );
        assertEquals(
                3,
                projectElement.descendantElementOrSelfStream(DependencyElement.class)
                        .flatMap(AnyPomElement::childElementStream)
                        .filter(e -> e.name().equals(new QName(MAVEN_POM_NS, "type")))
                        .count()
        );

        assertEquals(1, projectElement.descendantElementStream(PluginElement.class).count());
    }

    @Test
    void testElementClassNamesMatchingElementLocalNames() {
        ProjectElement projectElement = ProjectElement.from(doc1.documentElement());

        assertTrue(
                projectElement
                        .descendantElementOrSelfStream()
                        .filter(e -> !e.name().getNamespaceURI().equals(MAVEN_POM_NS))
                        .allMatch(otherElm ->
                                projectElement.descendantElementStream(PropertiesElement.class)
                                        .anyMatch(propsElm -> propsElm.descendantElementStream().anyMatch(de -> de.equals(otherElm)))
                        )
        );

        assertTrue(
                projectElement.descendantElementOrSelfStream()
                        .filter(e -> !(e instanceof OtherPomElement))
                        .allMatch(e -> e.getClass().getSimpleName().equals(
                                capitalize(e.name().getLocalPart()) + "Element"
                        ))
        );
    }

    @Test
    void testElementClassNamesMatchingElementLocalNamesInLargePom() {
        ProjectElement projectElement = ProjectElement.from(doc2.documentElement());
        Preconditions.checkState(projectElement.descendantElementOrSelfStream().count() >= 800);

        assertEquals(
                projectElement.backingElement().descendantElementOrSelfStream().count(),
                projectElement.descendantElementOrSelfStream().count()
        );

        assertEquals(
                projectElement.backingElement().descendantElementOrSelfStream(hasLocalName("dependency")).count(),
                projectElement.descendantElementOrSelfStream(DependencyElement.class).count()
        );
        assertEquals(
                projectElement.backingElement().descendantElementOrSelfStream(hasLocalName("plugin")).count(),
                projectElement.descendantElementOrSelfStream(PluginElement.class).count()
        );
        assertEquals(
                projectElement.backingElement().descendantElementOrSelfStream(hasLocalName("groupId")).count(),
                projectElement.descendantElementOrSelfStream(GroupIdElement.class).count()
        );

        assertTrue(
                projectElement.descendantElementOrSelfStream()
                        .filter(e -> !(e instanceof OtherPomElement))
                        .allMatch(e -> e.getClass().getSimpleName().equals(
                                capitalize(e.name().getLocalPart()) + "Element"
                        ))
        );
    }

    @Test
    void testTypesafeModelInLargePom() {
        ProjectElement projectElement = ProjectElement.from(doc2.documentElement());
        Preconditions.checkState(projectElement.descendantElementOrSelfStream().count() >= 800);

        List<AncestryAwareNodes.Element> otherPomElements = projectElement
                .descendantElementOrSelfStream(OtherPomElement.class)
                .map(AnyPomElement::backingElement)
                .toList();

        List<List<QName>> paths =
                otherPomElements.stream()
                        .map(AncestryAwareNodes.Element::elementNavigationPath)
                        .map(p -> getPath(projectElement, p))
                        .distinct()
                        .toList();

        List<List<QName>> nonExpectedPaths = paths.stream()
                .filter(p -> !p.contains(new QName(MAVEN_POM_NS, "properties"))
                        && !p.contains(new QName(MAVEN_POM_NS, "configuration"))
                        && !p.contains(new QName(MAVEN_POM_NS, "activation"))
                        && !p.contains(new QName(MAVEN_POM_NS, "snapshots"))
                )
                .toList();

        assertEquals(0, nonExpectedPaths.size());
    }

    @Test
    void testQueryDependencies() {
        PomProperties extraProperties = new PomProperties(ImmutableMap.of("revision", "0.0.1-SNAPSHOT"));

        ProjectElement projectElement = ProjectElement.from(doc1.documentElement());
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

    @Test
    void testQueryPlugins() {
        PomProperties extraProperties = new PomProperties(ImmutableMap.of("revision", "0.0.1-SNAPSHOT"));

        ProjectElement projectElement = ProjectElement.from(doc1.documentElement());
        // TODO Use super POM
        EffectivePom effectivePom = EffectivePom.from(projectElement);

        List<Dependency> pluginsAsDependencies = effectivePom.pluginsAsDependencies(extraProperties);

        Dependency expectedPluginAsDependency =
                new Dependency("org.apache.maven", "maven-compiler-plugin", "3.5.1");
        assertEquals(List.of(expectedPluginAsDependency), pluginsAsDependencies);
    }

    private static String capitalize(String string) {
        if (string.isEmpty()) {
            return string;
        }
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    private static List<QName> getPath(ProjectElement projectElement, ElementNavigationPath navPath) {
        return getPath(projectElement.backingElement().underlyingElement(), navPath);
    }

    private static List<QName> getPath(Element rootElement, ElementNavigationPath navPath) {
        if (navPath.isEmpty()) {
            return List.of(rootElement.name());
        } else {
            Element nextElement = rootElement.childElementStream().toList().get(navPath.getEntry(0));
            // Recursion
            return Stream.concat(Stream.of(rootElement.name()), getPath(nextElement, navPath.withoutFirstEntry()).stream()).toList();
        }
    }
}
