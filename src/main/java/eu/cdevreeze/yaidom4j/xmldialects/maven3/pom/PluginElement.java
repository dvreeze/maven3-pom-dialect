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

/**
 * Plugin element in a Maven POM file. It can occur in many locations in a POM file, but always having
 * the same XML structure.
 * <p>
 * No pluginManagement context is taken into account in the methods offered by this class.
 *
 * @author Chris de Vreeze
 */
public record PluginElement(Element backingElement) implements DependencyLikeElement {

    public record ExtensionsElement(Element backingElement) implements AnyPomElement {

        public ExtensionsElement {
            Preconditions.checkArgument(backingElement.name().equals(new QName(MAVEN_POM_NS, "extensions")));
        }
    }

    public record ExecutionElement(Element backingElement) implements AnyPomElement {

        public ExecutionElement {
            Preconditions.checkArgument(backingElement.name().equals(new QName(MAVEN_POM_NS, "execution")));
        }
    }

    public record ExecutionsElement(Element backingElement) implements AnyPomElement {

        public ExecutionsElement {
            Preconditions.checkArgument(backingElement.name().equals(new QName(MAVEN_POM_NS, "executions")));
        }

        public ImmutableList<ExecutionElement> executionElements() {
            return childElementStream(ExecutionElement.class).collect(ImmutableList.toImmutableList());
        }
    }

    public record GoalsElement(Element backingElement) implements AnyPomElement {

        public GoalsElement {
            Preconditions.checkArgument(backingElement.name().equals(new QName(MAVEN_POM_NS, "goals")));
        }
    }

    public PluginElement {
        Preconditions.checkArgument(backingElement.name().equals(new QName(MAVEN_POM_NS, "plugin")));
    }

    public Optional<PluginElement.ExtensionsElement> extensionsElementOption() {
        return childElementStream(PluginElement.ExtensionsElement.class).findFirst();
    }

    public Optional<PluginElement.ExecutionsElement> executionsElementOption() {
        return childElementStream(PluginElement.ExecutionsElement.class).findFirst();
    }

    public Optional<DependenciesElement> dependenciesElementOption() {
        return childElementStream(DependenciesElement.class).findFirst();
    }

    public Optional<PluginElement.GoalsElement> goalsElementOption() {
        return childElementStream(PluginElement.GoalsElement.class).findFirst();
    }
}
