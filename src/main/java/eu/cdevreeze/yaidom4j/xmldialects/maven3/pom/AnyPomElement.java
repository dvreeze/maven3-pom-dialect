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

import module eu.cdevreeze.yaidom4j;
import module java.base;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;

import java.util.Optional;
import java.util.function.Function;

/**
 * Any element in a Maven POM file.
 *
 * @author Chris de Vreeze
 */
public interface AnyPomElement {

    Element backingElement();

    default QName name() {
        return backingElement().name();
    }

    default Stream<AnyPomElement> selfElementStream() {
        return Stream.of(this);
    }

    default <T extends AnyPomElement> Stream<T> selfElementStream(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        var resultStream = selfElementStream()
                .filter(e -> clazz.isAssignableFrom(e.getClass()))
                .map(e -> (T) e);
        return resultStream;
    }

    default Stream<AnyPomElement> childElementStream() {
        return backingElement()
                .childElementStream()
                .map(AnyPomElement::create);
    }

    default <T extends AnyPomElement> Stream<T> childElementStream(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        var resultStream = childElementStream()
                .filter(e -> clazz.isAssignableFrom(e.getClass()))
                .map(e -> (T) e);
        return resultStream;
    }

    default Stream<AnyPomElement> descendantElementOrSelfStream() {
        return backingElement()
                .descendantElementOrSelfStream()
                .map(AnyPomElement::create);
    }

    default <T extends AnyPomElement> Stream<T> descendantElementOrSelfStream(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        var resultStream = descendantElementOrSelfStream()
                .filter(e -> clazz.isAssignableFrom(e.getClass()))
                .map(e -> (T) e);
        return resultStream;
    }

    default Stream<AnyPomElement> descendantElementStream() {
        return backingElement()
                .descendantElementStream()
                .map(AnyPomElement::create);
    }

    default <T extends AnyPomElement> Stream<T> descendantElementStream(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        var resultStream = descendantElementStream()
                .filter(e -> clazz.isAssignableFrom(e.getClass()))
                .map(e -> (T) e);
        return resultStream;
    }

    /**
     * Returns the raw text, without interpreting any variables, if any.
     */
    default String rawText() {
        return backingElement().text();
    }

    /**
     * Returns the raw text stripped of surrounding whitespace, without interpreting any variables, if any.
     */
    default String rawStrippedText() {
        return backingElement().text().strip();
    }

    /**
     * Returns the stripped text value, after resolving any properties used in it.
     */
    default String resolvedValue(PomProperties properties) {
        return properties.expandInString(rawStrippedText());
    }

    static AnyPomElement create(Element backingElement) {
        return Optional.ofNullable(constructorMap.get(backingElement.name()))
                .map(func -> func.apply(backingElement))
                .orElse(new OtherPomElement(backingElement));
    }

    String NS = "http://maven.apache.org/POM/4.0.0";

    ImmutableMap<QName, Function<Element, AnyPomElement>> constructorMap =
            ImmutableMap.<QName, Function<Element, AnyPomElement>>builder()
                    .put(new QName(NS, "project"), ProjectElement::new)
                    .put(new QName(NS, "modelVersion"), ModelVersionElement::new)
                    .put(new QName(NS, "parent"), ParentElement::new)
                    .put(new QName(NS, "groupId"), GroupIdElement::new)
                    .put(new QName(NS, "artifactId"), ArtifactIdElement::new)
                    .put(new QName(NS, "version"), VersionElement::new)
                    .put(new QName(NS, "packaging"), PackagingElement::new)
                    .put(new QName(NS, "name"), NameElement::new)
                    .put(new QName(NS, "description"), DescriptionElement::new)
                    .put(new QName(NS, "url"), UrlElement::new)
                    .put(new QName(NS, "modules"), ModulesElement::new)
                    .put(new QName(NS, "module"), ModuleElement::new)
                    .put(new QName(NS, "properties"), PropertiesElement::new)
                    .put(new QName(NS, "dependencyManagement"), DependencyManagementElement::new)
                    .put(new QName(NS, "dependencies"), DependenciesElement::new)
                    .put(new QName(NS, "dependency"), DependencyElement::new)
                    .put(new QName(NS, "reporting"), ReportingElement::new)
                    .put(new QName(NS, "build"), BuildElement::new)
                    .build();
}
