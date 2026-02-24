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
import module com.google.common;

import java.util.Objects;
import java.util.Optional;

/**
 * Properties in a POM file. This class offers methods to resolve properties in any string value in a POM.
 * <p>
 * For more details, see <a href="https://maven.apache.org/pom.html#Properties">POM properties</a>.
 *
 * @author Chris de Vreeze
 */
public record PomProperties(ImmutableMap<String, String> properties) {

    private static final int MAX_RECURSION_DEPTH = 100;

    public PomProperties {
        Preconditions.checkArgument(properties.keySet().stream().allMatch(this::isAllowedPropertyName));
        Preconditions.checkArgument(properties.values().stream().allMatch(this::isAllowedPropertyValue));
    }

    public static PomProperties empty() {
        return new PomProperties(ImmutableMap.of());
    }

    public String expandInString(String value) {
        return expandInString(value, 0);
    }

    public PomProperties add(PomProperties addedPomProperties) {
        ImmutableMap<String, String> resultProps = ImmutableMap.<String, String>builder()
                .putAll(this.properties())
                .putAll(addedPomProperties.properties())
                .buildKeepingLast();
        return new PomProperties(resultProps);
    }

    private boolean isAllowedPropertyName(String property) {
        return property.codePoints().allMatch(c -> Character.isLetterOrDigit(c) || Character.toString(c).equals("."));
    }

    private boolean isAllowedPropertyValue(String value) {
        return !value.contains("${");
    }

    private String expandInString(String value, int recursionDepth) {
        Preconditions.checkArgument(recursionDepth <= MAX_RECURSION_DEPTH);

        int idx = value.indexOf("${");
        if (idx < 0) {
            return value;
        } else {
            StringBuilder sb = new StringBuilder();

            int nextIdx = value.indexOf("}", idx + 1);
            Preconditions.checkState(nextIdx >= 0, "No closing brace characters found");

            sb.append(value, 0, idx);

            String propertyName = value.substring(idx + 2, nextIdx);
            Preconditions.checkState(!propertyName.isEmpty(), "Empty property name found");
            String propertyValue = Objects.requireNonNull(tryToResolveProperty(propertyName).orElseThrow());

            sb.append(propertyValue).append(value.substring(nextIdx + 1));

            // Recursion
            return expandInString(sb.toString(), recursionDepth + 1);
        }
    }

    private Optional<String> tryToResolveProperty(String propertyName) {
        // TODO Resolve environment variables, system properties, etc.
        // See https://www.sonatype.com/maven-complete-reference/properties-and-resource-filtering
        return Optional.ofNullable(properties.get(propertyName));
    }
}
