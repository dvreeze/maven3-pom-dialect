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

import com.google.common.collect.ImmutableList;

/**
 * Effective POM.
 *
 * @author Chris de Vreeze
 */
public record EffectivePom(ProjectElement projectElement) {

    private static final String APACHE_MAVEN_BASE_GROUP_ID = "org.apache.maven";

    // TODO Check that the POM is indeed an effective POM, so a "closed world"
    // Typically that means that the POM contains no explicit parent

    public static EffectivePom from(ProjectElement projectElement) {
        return new EffectivePom(projectElement);
    }

    /**
     * Returns the "target artifact" as {@link Dependency}. Fails for the super POM.
     */
    public Dependency artifactAsDependency(PomProperties extraProperties) {
        PomProperties pomProperties = projectElement().resultProperties(PomProperties.empty())
                .add(extraProperties);

        return new Dependency(
                projectElement().groupIdOption(pomProperties).orElse(APACHE_MAVEN_BASE_GROUP_ID),
                projectElement().artifactIdOption(pomProperties).orElseThrow(),
                projectElement().versionOption(pomProperties).orElseThrow()
        );
    }

    /**
     * Returns the dependencies in the "dependencies" section, not the "dependencyManagement" section.
     */
    public ImmutableList<Dependency> dependencies(PomProperties extraProperties) {
        PomProperties pomProperties = projectElement().resultProperties(PomProperties.empty())
                .add(extraProperties);

        return projectElement().dependenciesElementOption()
                .map(elm -> elm.dependencyElements()
                        .stream()
                        .map(e -> new Dependency(
                                e.groupIdOption(pomProperties).orElse(APACHE_MAVEN_BASE_GROUP_ID),
                                e.artifactIdOption(pomProperties).orElseThrow(),
                                e.versionOption(pomProperties).orElseThrow()
                        ))
                        .collect(ImmutableList.toImmutableList()))
                .orElse(ImmutableList.of());
    }
}
