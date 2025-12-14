# Publishing

An assortment of problems I've encountered with maven publishing via Gradle and how to solve them.

## The gradle `MavenPublication` is including dependencies I marked as `implementation` and `runtimeOnly` and I don't want that

I addressed this by transforming the output XML via `pom.withXml { ... }`.

```groovy
publishing {
    publications {
        myLib(MavenPublication) {
            // This will add the dependencies you don't want, we'll just transform the xml after the fact
            from components.java
            
            pom.withXml {
                // getAt(String) wraps the results in 'NodeList'
                // NodeList extends ArrayList, so we can do 'removeIf(Predicate<T>)'
                // So we extract the single item with 'get(0)'
                def node = asNode().getAt('dependencies').get(0)
                
                // Remove block nodes of 'implementation' and 'runtimeOnly' dependencies
                node.children().removeIf(c -> isRuntimeDependency(c))
            }
        }
    }
    
    // I'm publishing to a local directory.
    repositories {
        maven {
            name = 'staging_deploy'
            url = layout.buildDirectory.dir('staging-deploy')
        }
    }
}

// Dependencies get mapped to 'compile' or 'runtime' scoped entries in XML.
//
// This assumes your setup is set up such that:
//  - api: Consumers must resolve this dependency I use too
//  - implementation: Consumers are not required to resolve this dependency I use
//
// In my setup this means I want to remove anything that is output as a 'runtime' XML dependency.
// If your setup is different its on you to change how this method works.
// 
// To figure out what calls you can make, see:
//  - https://docs.groovy-lang.org/latest/html/api/groovy/util/Node.html
//  - https://docs.groovy-lang.org/latest/html/api/groovy/util/NodeList.html
private static boolean isRuntimeDependency(Node node) {
    String scope = node.get("scope").text()
    return "runtime".equals(scope)
}
```

## My publish task can't use `from components.java` so how do I make the `dependencies` block now?

Under `publishing.publications.mavenJava.pom` use the following:
```groovy
withXml {
    // Use different configurations based on your needs
    def allDeps = project.configurations.runtimeClasspath.resolvedConfiguration.firstLevelModuleDependencies +
            project.configurations.compileClasspath.resolvedConfiguration.firstLevelModuleDependencies +
            project.configurations.testRuntimeClasspath.resolvedConfiguration.firstLevelModuleDependencies +
            project.configurations.testCompileClasspath.resolvedConfiguration.firstLevelModuleDependencies
    def root = asNode()
    def dependenciesNode = root.appendNode("dependencies")
    allDeps.each { d ->
        def depNode = dependenciesNode.appendNode("dependency")
        depNode.appendNode("groupId", d.moduleGroup)
        depNode.appendNode("artifactId", d.name)
        depNode.appendNode("version", d.moduleVersion)
    }
```

## Using the `java-test-fixtures` adds the project as a dependency in the pom to itself, breaking `:publish`

Just don't use `java-test-fixtures`. I know, that _"tip"_ sucks, but so does Gradle.

```groovy
sourceSets {
    test {
        java {
            srcDirs 'src/testFixtures/java', 'src/test/java'
        }
    }
}
```
