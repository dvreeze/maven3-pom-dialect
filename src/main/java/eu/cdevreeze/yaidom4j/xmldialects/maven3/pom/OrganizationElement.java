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
import eu.cdevreeze.yaidom4j.dom.ancestryaware.AncestryAwareNodes.Element;

import java.util.Optional;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.AncestryAwareElementPredicates.hasName;

/**
 * Organization element in a Maven POM file, as child element of the root element.
 *
 * @author Chris de Vreeze
 */
public record OrganizationElement(Element backingElement) implements AnyPomElement {

    public OrganizationElement {
        Preconditions.checkArgument(backingElement.name().equals(new QName(MAVEN_POM_NS, "organization")));
        Preconditions.checkArgument(backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "project")).isPresent());
    }

    public Optional<NameElement> nameElementOption() {
        return childElementStream(NameElement.class).findFirst();
    }

    public Optional<String> nameOption(PomProperties properties) {
        return nameElementOption().map(e -> e.resolvedValue(properties));
    }

    public Optional<UrlElement> urlElementOption() {
        return childElementStream(UrlElement.class).findFirst();
    }

    public Optional<String> urlOption(PomProperties properties) {
        return urlElementOption().map(e -> e.resolvedValue(properties));
    }
}
