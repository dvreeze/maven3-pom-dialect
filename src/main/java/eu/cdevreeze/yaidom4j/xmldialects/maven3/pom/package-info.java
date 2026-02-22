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

/**
 * XML dialect support for Maven 3 POM files, as wrappers around {@link eu.cdevreeze.yaidom4j.dom.immutabledom.Element}.
 * See <a href="https://maven.apache.org/pom.html">POM Reference</a>. These wrappers are foremost syntactic
 * in nature.
 * <p>
 * See <a href="https://maven.apache.org/xsd/maven-4.0.0.xsd">maven-4.0.0.xsd</a> for the POM XSD file.
 * <p>
 * Also see <a href="https://www.sonatype.com/maven-complete-reference">Maven Complete Reference</a>.
 * <p>
 * See <a href="https://maven.apache.org/ref/3.0.3/maven-model/apidocs/index.html">Maven Model API doc</a>
 * for the API documentation of Maven itself as Java library.
 *
 * @author Chris de Vreeze
 */
@NullMarked
package eu.cdevreeze.yaidom4j.xmldialects.maven3.pom;

import org.jspecify.annotations.NullMarked;
