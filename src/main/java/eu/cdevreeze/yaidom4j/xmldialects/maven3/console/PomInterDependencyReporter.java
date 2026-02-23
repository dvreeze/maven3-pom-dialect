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

package eu.cdevreeze.yaidom4j.xmldialects.maven3.console;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.yaidom4j.core.NamespaceScope;
import eu.cdevreeze.yaidom4j.dom.immutabledom.*;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParser;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinter;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinters;
import eu.cdevreeze.yaidom4j.xmldialects.maven3.pom.PomFile;
import eu.cdevreeze.yaidom4j.xmldialects.maven3.pom.ProjectElement;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.cdevreeze.yaidom4j.xmldialects.maven3.pom.AnyPomElement.NS;

/**
 * Program that shows how a set of POM files "hang together", in terms of Maven modules and parent POMs.
 * The input is a set of root directories, from where POM files are searched. The output is a report
 * of interdependencies between these POM files in terms of modules and parent POMs. This output is in
 * XML format.
 * <p>
 * Only POM files named "pom.xml" are considered.
 *
 * @author Chris de Vreeze
 */
public class PomInterDependencyReporter {

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);
        ImmutableList<Path> rootDirectories = Arrays.stream(args)
                .map(Path::of)
                .peek(Files::isDirectory)
                .collect(ImmutableList.toImmutableList());

        PomInterDependencyReporter reporter = new PomInterDependencyReporter();
        Document resultDoc = reporter.generateReport(rootDirectories);

        DocumentPrinter documentPrinter = DocumentPrinters.instance();
        documentPrinter.print(resultDoc, new StreamResult(System.out));
    }

    public Document generateReport(ImmutableList<Path> rootDirectories) {
        DocumentParser documentParser = DocumentParsers.instance();

        ImmutableList<PomFile> pomFiles = collectPoms(rootDirectories, documentParser);

        return generateReportForPoms(pomFiles);
    }

    public Document generateReportForPoms(ImmutableList<PomFile> pomFiles) {
        ImmutableList<Node> childElems =
                pomFiles.stream().map(this::generateReportForPom).collect(ImmutableList.toImmutableList());
        Element rootElem = Nodes.elem(new QName("pomFiles"))
                .plusChildren(childElems)
                .notUndeclaringPrefixes(NamespaceScope.of("", NS))
                .removeInterElementWhitespace();
        return new Document(Optional.empty(), ImmutableList.of(rootElem));
    }

    private Element generateReportForPom(PomFile pomFile) {
        // Stripping everything from the POM, except Maven coordinates, parent and modules
        Element strippedProjectElement = pomFile.projectElement().backingElement()
                .transformChildElementsToNodeLists(che ->
                        switch (che.name().getLocalPart()) {
                            case "modelVersion", "groupId", "artifactId", "version", "modules", "parent" ->
                                    List.of(che);
                            default -> List.of();
                        }
                )
                .transformDescendantElementsOrSelf(e ->
                        e.withChildren(
                                e.children()
                                        .stream()
                                        .filter(this::isElementOrText)
                                        .collect(ImmutableList.toImmutableList())
                        )
                );
        return Nodes.elem(new QName("pomFile"))
                .plusChild(Nodes.elem(new QName("path")).plusText(pomFile.path().toString()))
                .plusChild(strippedProjectElement);
    }

    private ImmutableList<PomFile> collectPoms(ImmutableList<Path> rootDirectories, DocumentParser documentParser) {
        return rootDirectories
                .stream()
                .flatMap(dir -> collectPoms(dir, documentParser).stream())
                .collect(ImmutableList.toImmutableList());
    }

    private ImmutableList<PomFile> collectPoms(Path rootDirectory, DocumentParser documentParser) {
        Preconditions.checkArgument(Files.isDirectory(rootDirectory));

        ImmutableList<PomFile> pomFiles;

        // Does not follow symbolic links
        try (Stream<Path> paths = Files.walk(rootDirectory, MAX_DIR_DEPTH)) {
            pomFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().equals(Path.of("pom.xml")))
                    .map(p -> parsePom(p, documentParser))
                    .collect(ImmutableList.toImmutableList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return pomFiles;
    }

    private PomFile parsePom(Path path, DocumentParser documentParser) {
        Preconditions.checkArgument(Files.isRegularFile(path));

        Element rootElement = documentParser.parse(path.toUri()).documentElement();
        Preconditions.checkState(rootElement.name().equals(new QName(NS, "project")));
        return new PomFile(ProjectElement.from(rootElement), path);
    }

    private boolean isElementOrText(Node node) {
        return node instanceof Element || node instanceof Text;
    }

    private static final int MAX_DIR_DEPTH = 100;
}
