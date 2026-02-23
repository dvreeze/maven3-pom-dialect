# Maven3-pom-dialect

XML dialect support for Maven 3 POM files, based on the [yaidom4j](https://github.com/dvreeze/yaidom4j)
library. The querying experience is like for yaidom4j elements, but more type-safe w.r.t. the contents
of a Maven 3 POM file.

The point of this library is to be able to inspect (and if needed manipulate) POM files as XML sources.
Many useful validations belong to the abstraction level of POM files as syntactic XML files. Also, at
that low abstraction level many useful reports can be generated. For example, in a very large code base
containing many POM files reports can be generated that show a graph of Maven modules and parent POMs,
with the nodes being identified by Maven coordinates accompanied by full file paths of those POM files.

This library does not compute any effective POMs, but that is hardly needed, given that Maven can easily
generate those effective POM files for us. Still, an effective POM can also be fed to this library for
inspection at the low abstraction level of POM documents as XML (obeying the XML dialect for POM files,
that is, being valid according to the XML Schema for Maven POM files).
