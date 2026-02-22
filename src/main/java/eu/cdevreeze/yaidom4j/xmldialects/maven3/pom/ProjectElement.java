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
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;

import javax.xml.namespace.QName;
import java.util.Optional;

/**
 * Project root element of a Maven POM file.
 *
 * @author Chris de Vreeze
 */
public record ProjectElement(Element backingElement) implements DependencyLikeElement {

    public ProjectElement {
        Preconditions.checkArgument(backingElement.name().equals(new QName(NS, "project")));
    }

    public static ProjectElement from(Element backingElement) {
        return new ProjectElement(backingElement);
    }

    public Dependency artifactAsDependency(ParentContext parentContext, PomProperties properties) {
        return new Dependency(
                groupIdOption(properties)
                        .or(() -> parentContext.groupIdOption(properties))
                        .orElseThrow(),
                artifactIdOption(properties).orElseThrow(),
                versionOption(properties)
                        .or(() -> parentContext.versionOption(properties))
                        .orElseThrow()
        );
    }

    public Optional<ModelVersionElement> modelVersionElementOption() {
        return childElementStream(ModelVersionElement.class).findFirst();
    }

    public Optional<ParentElement> parentElementOption() {
        return childElementStream(ParentElement.class).findFirst();
    }

    public Optional<PackagingElement> packagingElementOption() {
        return childElementStream(PackagingElement.class).findFirst();
    }

    public String packaging(PomProperties properties) {
        return packagingElementOption().map(e -> e.resolvedValue(properties)).orElse("jar");
    }

    public Optional<NameElement> nameElementOption() {
        return childElementStream(NameElement.class).findFirst();
    }

    public Optional<String> nameOption(PomProperties properties) {
        return nameElementOption().map(e -> e.resolvedValue(properties));
    }

    public Optional<DescriptionElement> descriptionElementOption() {
        return childElementStream(DescriptionElement.class).findFirst();
    }

    public Optional<String> descriptionOption(PomProperties properties) {
        return descriptionElementOption().map(e -> e.resolvedValue(properties));
    }

    public Optional<UrlElement> urlElementOption() {
        return childElementStream(UrlElement.class).findFirst();
    }

    public Optional<String> urlOption(PomProperties properties) {
        return urlElementOption().map(e -> e.resolvedValue(properties));
    }

    public Optional<ModulesElement> modulesElementOption() {
        return childElementStream(ModulesElement.class).findFirst();
    }

    public ImmutableList<String> modules(PomProperties properties) {
        return modulesElementOption()
                .map(e -> e.modules(properties))
                .orElse(ImmutableList.of());
    }

    public Optional<DependencyManagementElement> dependencyManagementElementOption() {
        return childElementStream(DependencyManagementElement.class).findFirst();
    }

    public Optional<DependenciesElement> dependenciesElementOption() {
        return childElementStream(DependenciesElement.class).findFirst();
    }
}
