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
import java.util.function.Function;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.AncestryAwareElementPredicates.hasName;

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

    String MAVEN_POM_NS = "http://maven.apache.org/POM/4.0.0";

    ImmutableMap<QName, Function<Element, AnyPomElement>> constructorMap =
            ImmutableMap.<QName, Function<Element, AnyPomElement>>builder()
                    .put(new QName(MAVEN_POM_NS, "project"), ProjectElement::new)
                    .put(new QName(MAVEN_POM_NS, "modelVersion"), ModelVersionElement::new)
                    .put(new QName(MAVEN_POM_NS, "parent"), ParentElement::new)
                    .put(new QName(MAVEN_POM_NS, "groupId"), GroupIdElement::new)
                    .put(new QName(MAVEN_POM_NS, "artifactId"), ArtifactIdElement::new)
                    .put(new QName(MAVEN_POM_NS, "version"), VersionElement::new)
                    .put(new QName(MAVEN_POM_NS, "packaging"), PackagingElement::new)
                    .put(new QName(MAVEN_POM_NS, "name"), NameElement::new)
                    .put(new QName(MAVEN_POM_NS, "description"), DescriptionElement::new)
                    .put(new QName(MAVEN_POM_NS, "url"), UrlElement::new)
                    .put(new QName(MAVEN_POM_NS, "inceptionYear"), InceptionYearElement::new)
                    .put(new QName(MAVEN_POM_NS, "organization"), AnyPomElement::organizationElement)
                    .put(new QName(MAVEN_POM_NS, "licenses"), LicensesElement::new)
                    .put(new QName(MAVEN_POM_NS, "license"), LicenseElement::new)
                    .put(new QName(MAVEN_POM_NS, "developers"), DevelopersElement::new)
                    .put(new QName(MAVEN_POM_NS, "developer"), DeveloperElement::new)
                    .put(new QName(MAVEN_POM_NS, "contributors"), ContributorsElement::new)
                    .put(new QName(MAVEN_POM_NS, "contributor"), ContributorElement::new)
                    .put(new QName(MAVEN_POM_NS, "mailingLists"), MailingListsElement::new)
                    .put(new QName(MAVEN_POM_NS, "mailingList"), MailingListElement::new)
                    .put(new QName(MAVEN_POM_NS, "prerequisites"), PrerequisitesElement::new)
                    .put(new QName(MAVEN_POM_NS, "modules"), ModulesElement::new)
                    .put(new QName(MAVEN_POM_NS, "module"), ModuleElement::new)
                    .put(new QName(MAVEN_POM_NS, "scm"), ScmElement::new)
                    .put(new QName(MAVEN_POM_NS, "issueManagement"), IssueManagementElement::new)
                    .put(new QName(MAVEN_POM_NS, "ciManagement"), CiManagementElement::new)
                    .put(new QName(MAVEN_POM_NS, "distributionManagement"), DistributionManagementElement::new)
                    .put(new QName(MAVEN_POM_NS, "properties"), PropertiesElement::new)
                    .put(new QName(MAVEN_POM_NS, "dependencyManagement"), DependencyManagementElement::new)
                    .put(new QName(MAVEN_POM_NS, "dependencies"), DependenciesElement::new)
                    .put(new QName(MAVEN_POM_NS, "dependency"), DependencyElement::new)
                    .put(new QName(MAVEN_POM_NS, "repositories"), RepositoriesElement::new)
                    .put(new QName(MAVEN_POM_NS, "repository"), RepositoryElement::new)
                    .put(new QName(MAVEN_POM_NS, "pluginRepositories"), PluginRepositoriesElement::new)
                    .put(new QName(MAVEN_POM_NS, "pluginRepository"), PluginRepositoryElement::new)
                    .put(new QName(MAVEN_POM_NS, "build"), AnyPomElement::buildElement)
                    .put(new QName(MAVEN_POM_NS, "reports"), AnyPomElement::reportsElement)
                    .put(new QName(MAVEN_POM_NS, "reporting"), ReportingElement::new)
                    .put(new QName(MAVEN_POM_NS, "pluginManagement"), PluginManagementElement::new)
                    .put(new QName(MAVEN_POM_NS, "plugins"), PluginsElement::new)
                    .put(new QName(MAVEN_POM_NS, "plugin"), PluginElement::new)
                    .put(new QName(MAVEN_POM_NS, "profiles"), ProfilesElement::new)
                    .put(new QName(MAVEN_POM_NS, "profile"), ProfileElement::new)
                    .put(new QName(MAVEN_POM_NS, "comments"), CommentsElement::new)
                    .put(new QName(MAVEN_POM_NS, "configuration"), ConfigurationElement::new)
                    .put(new QName(MAVEN_POM_NS, "distribution"), DistributionElement::new)
                    .put(new QName(MAVEN_POM_NS, "executions"), ExecutionsElement::new)
                    .put(new QName(MAVEN_POM_NS, "execution"), ExecutionElement::new)
                    .put(new QName(MAVEN_POM_NS, "notifiers"), NotifiersElement::new)
                    .put(new QName(MAVEN_POM_NS, "notifier"), NotifierElement::new)
                    .put(new QName(MAVEN_POM_NS, "scope"), ScopeElement::new)
                    .put(new QName(MAVEN_POM_NS, "type"), TypeElement::new)
                    .put(new QName(MAVEN_POM_NS, "system"), SystemElement::new)
                    .put(new QName(MAVEN_POM_NS, "id"), IdElement::new)
                    .put(new QName(MAVEN_POM_NS, "phase"), PhaseElement::new)
                    .put(new QName(MAVEN_POM_NS, "goals"), AnyPomElement::goalsElement)
                    .put(new QName(MAVEN_POM_NS, "goal"), AnyPomElement::goalElement)
                    .put(new QName(MAVEN_POM_NS, "activation"), ActivationElement::new)
                    .put(new QName(MAVEN_POM_NS, "classifier"), ClassifierElement::new)
                    .put(new QName(MAVEN_POM_NS, "exclusions"), ExclusionsElement::new)
                    .put(new QName(MAVEN_POM_NS, "exclusion"), ExclusionElement::new)
                    .put(new QName(MAVEN_POM_NS, "extensions"), AnyPomElement::extensionsElement)
                    .put(new QName(MAVEN_POM_NS, "extension"), AnyPomElement::extensionElement)
                    .put(new QName(MAVEN_POM_NS, "optional"), OptionalElement::new)
                    .put(new QName(MAVEN_POM_NS, "relativePath"), RelativePathElement::new)
                    .put(new QName(MAVEN_POM_NS, "systemPath"), SystemPathElement::new)
                    .build();

    private static AnyPomElement organizationElement(Element backingElement) {
        if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "project")).isPresent()) {
            return new OrganizationElement(backingElement);
        } else {
            return new OtherPomElement(backingElement);
        }
    }

    private static AnyPomElement buildElement(Element backingElement) {
        if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "project")).isPresent()) {
            return new BuildElement(backingElement);
        } else if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "profile")).isPresent()) {
            return new ProfileElement.BuildElement(backingElement);
        } else {
            return new OtherPomElement(backingElement);
        }
    }

    private static AnyPomElement reportsElement(Element backingElement) {
        // Can occur in multiple locations in a POM file
        return new OtherPomElement(backingElement);
    }

    private static AnyPomElement goalsElement(Element backingElement) {
        if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "execution")).isPresent()) {
            // For XSD type "PluginExecution" as parent element type
            return new GoalsElement(backingElement);
        } else if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "plugin")).isPresent()) {
            // For XSD type "Plugin" as parent element type
            return new PluginElement.GoalsElement(backingElement);
        } else {
            return new OtherPomElement(backingElement);
        }
    }

    private static AnyPomElement goalElement(Element backingElement) {
        if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "goals")).isPresent()) {
            var grandParentElementOption = backingElement.parentElementOption().flatMap(Element::parentElementOption);
            if (grandParentElementOption.filter(hasName(MAVEN_POM_NS, "execution")).isPresent()) {
                // For XSD type "PluginExecution" as grandparent element type
                return new GoalElement(backingElement);
            } else {
                return new OtherPomElement(backingElement);
            }
        } else {
            return new OtherPomElement(backingElement);
        }
    }

    private static AnyPomElement extensionsElement(Element backingElement) {
        if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "build")).isPresent()) {
            // For XSD type "Build" as parent element type
            return new ExtensionsElement(backingElement);
        } else if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "plugin")).isPresent()) {
            // For XSD type "Plugin" as parent element type
            return new PluginElement.ExtensionsElement(backingElement);
        } else {
            return new OtherPomElement(backingElement);
        }
    }

    private static AnyPomElement extensionElement(Element backingElement) {
        if (backingElement.parentElementOption().filter(hasName(MAVEN_POM_NS, "extensions")).isPresent()) {
            var grandParentElementOption = backingElement.parentElementOption().flatMap(Element::parentElementOption);
            if (grandParentElementOption.filter(hasName(MAVEN_POM_NS, "build")).isPresent()) {
                // For XSD type "Build" as grandparent element type
                return new ExtensionElement(backingElement);
            } else {
                return new OtherPomElement(backingElement);
            }
        } else {
            return new OtherPomElement(backingElement);
        }
    }
}
