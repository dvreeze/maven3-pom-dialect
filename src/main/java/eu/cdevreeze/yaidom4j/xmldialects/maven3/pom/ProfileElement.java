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
 * Profile element in a Maven POM file.
 *
 * @author Chris de Vreeze
 */
public record ProfileElement(Element backingElement) implements AnyPomElement {

    public record BuildElement(Element backingElement) implements AnyPomElement {

        public BuildElement {
            Preconditions.checkArgument(backingElement.name().equals(new QName(MAVEN_POM_NS, "build")));
            Preconditions.checkArgument(backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "profile")).isPresent());
        }
    }

    public ProfileElement {
        Preconditions.checkArgument(backingElement.name().equals(new QName(MAVEN_POM_NS, "profile")));
    }

    public Optional<IdElement> idElementOption() {
        return childElementStream(IdElement.class).findFirst();
    }

    public Optional<ActivationElement> activationElementOption() {
        return childElementStream(ActivationElement.class).findFirst();
    }

    public Optional<ProfileElement.BuildElement> buildElementOption() {
        return childElementStream(ProfileElement.BuildElement.class).findFirst();
    }

    public Optional<ModulesElement> modulesElementOption() {
        return childElementStream(ModulesElement.class).findFirst();
    }

    public Optional<DistributionManagementElement> distributionManagementElementOption() {
        return childElementStream(DistributionManagementElement.class).findFirst();
    }

    public Optional<PropertiesElement> propertiesElementOption() {
        return childElementStream(PropertiesElement.class).findFirst();
    }

    public Optional<DependencyManagementElement> dependencyManagementElementOption() {
        return childElementStream(DependencyManagementElement.class).findFirst();
    }

    public Optional<DependenciesElement> dependenciesElementOption() {
        return childElementStream(DependenciesElement.class).findFirst();
    }

    public Optional<RepositoriesElement> repositoriesElementOption() {
        return childElementStream(RepositoriesElement.class).findFirst();
    }

    public Optional<PluginRepositoriesElement> pluginRepositoriesElementOption() {
        return childElementStream(PluginRepositoriesElement.class).findFirst();
    }

    public Optional<ReportingElement> reportingElementOption() {
        return childElementStream(ReportingElement.class).findFirst();
    }
}
