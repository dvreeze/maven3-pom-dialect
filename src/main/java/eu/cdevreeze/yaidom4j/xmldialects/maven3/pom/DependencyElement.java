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

import static eu.cdevreeze.yaidom4j.dom.immutabledom.ElementPredicates.hasName;

/**
 * Dependency element in a Maven POM file.
 * <p>
 * No dependencyManagement context is taken into account in the methods offered by this class.
 *
 * @author Chris de Vreeze
 */
public record DependencyElement(Element backingElement) implements DependencyLikeElement {

    public DependencyElement {
        Preconditions.checkArgument(backingElement.name().equals(new QName(NS, "dependency")));
    }

    public Optional<OtherPomElement> classifierElementOption() {
        return backingElement()
                .childElementStream(hasName(NS, "classifier"))
                .findFirst()
                .map(OtherPomElement::new);
    }
}
