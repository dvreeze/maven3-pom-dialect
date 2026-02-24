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
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;

/**
 * Modules element in a Maven POM file.
 *
 * @author Chris de Vreeze
 */
public record ModulesElement(Element backingElement) implements AnyPomElement {

    public ModulesElement {
        Preconditions.checkArgument(backingElement.name().equals(new QName(NS, "modules")));
    }

    public ImmutableList<ModuleElement> moduleElements() {
        return childElementStream(ModuleElement.class).collect(ImmutableList.toImmutableList());
    }

    public ImmutableList<String> modules(PomProperties properties) {
        return moduleElements()
                .stream()
                .map(e -> e.resolvedValue(properties))
                .collect(ImmutableList.toImmutableList());
    }
}
