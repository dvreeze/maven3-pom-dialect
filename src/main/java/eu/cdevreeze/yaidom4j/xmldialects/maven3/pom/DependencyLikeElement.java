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

import java.util.Optional;

/**
 * Any "dependency"-like element in a Maven POM file.
 *
 * @author Chris de Vreeze
 */
public interface DependencyLikeElement extends AnyPomElement {

    default Optional<GroupIdElement> groupIdElementOption() {
        return childElementStream(GroupIdElement.class).findFirst();
    }

    default Optional<String> groupIdOption(PomProperties properties) {
        return groupIdElementOption().map(e -> e.groupId(properties));
    }

    default Optional<ArtifactIdElement> artifactIdElementOption() {
        return childElementStream(ArtifactIdElement.class).findFirst();
    }

    default Optional<String> artifactIdOption(PomProperties properties) {
        return artifactIdElementOption().map(e -> e.artifactId(properties));
    }

    default Optional<VersionElement> versionElementOption() {
        return childElementStream(VersionElement.class).findFirst();
    }

    default Optional<String> versionOption(PomProperties properties) {
        return versionElementOption().map(e -> e.version(properties));
    }
}
