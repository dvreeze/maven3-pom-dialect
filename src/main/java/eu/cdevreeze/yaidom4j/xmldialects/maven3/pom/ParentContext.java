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

import javax.xml.namespace.QName;
import java.util.Optional;

/**
 * Inherited elements from the parent POM.
 *
 * @author Chris de Vreeze
 */
public record ParentContext(ImmutableList<AnyPomElement> pomElements) {

    public ParentContext {
        Preconditions.checkArgument(
                pomElements.stream().noneMatch(e -> e.name().equals(new QName(AnyPomElement.NS, "artifactId")))
        );
    }

    public static ParentContext empty() {
        return new ParentContext(ImmutableList.of());
    }

    public Optional<String> groupIdOption(PomProperties properties) {
        return pomElements()
                .stream()
                .filter(e -> e.name().equals(new QName(AnyPomElement.NS, "groupId")))
                .map(e -> (GroupIdElement) e)
                .findFirst()
                .map(e -> e.groupId(properties));
    }

    public Optional<String> versionOption(PomProperties properties) {
        return pomElements()
                .stream()
                .filter(e -> e.name().equals(new QName(AnyPomElement.NS, "version")))
                .map(e -> (VersionElement) e)
                .findFirst()
                .map(e -> e.version(properties));
    }
}
