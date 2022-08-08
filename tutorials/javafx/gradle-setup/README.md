# Setting up JavaFX with Gradle

> If you don't already know what Gradle is, check out this [introduction post](https://www.baeldung.com/gradle).

Gradle isn't short and simple so no TLDR for you. If you want something simple, go use [Maven](../maven-setup/README.md). 

## 1: Creating the basic Java application `build.gradle`

It should generally start out looking like this:
```groovy
plugins {
    id 'java'
}

group 'org.example'
version '1.0.0'

def mainClassName = "org.example.JavaFxDemo"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // stuff goes here
}
```

## 2. Create a custom dependency configuration

A custom configuration is required. Because we cannot use `implementation` dependencies in our `farJar` task we create later _(Because Gradle changed how they handle dependencies)_. We cannot use `compile` since it has been removed in recent Gradle releases because they follow _"move fast and break things"_ rather than making a stable tool.

Anyways, you'll create a default configuration and make implementation extend from it.
```groovy
configurations {
    compileAndRuntime
    compileAndRuntime.transitive = true
    implementation.extendsFrom(compileAndRuntime)
}
```

## 3: Adding the JavaFX dependencies

For looking up maven artifacts, I prefer `mvnrepository.com` over the official `maven.org` since it makes it much easier to quickly find what I want and copy and paste it into my projects. 
With this in mind, to get the JavaFX dependencies you can find them all located here: [mvnrepository.com/artifact/org.openjfx](https://mvnrepository.com/artifact/org.openjfx)

When you click on an artifact it will list all the available versions for it. Usually you're safe to select the most up-to-date version. 
Once selected it will show you a series of tabs for `Maven`, `Gradle`, and a few others. Make sure `Gradle` is selected and copy the `groovy` text provided.

Now switch over to your project and open the [`build.gradle`](build.gradle). Find the `dependencies { ... }` block, or create one if it does not exist. Then paste it in. Though for JavaFX specifically we'll be modifying things a bit. To make changing out versions easily we want to use variables to specify our JFX version. We also need to include the classifier for our specific platform.

```groovy
dependencies {
    def jfxVersion = "16"
    def jfxPlatform = getJavaFxPlatform()
    compileAndRuntime "org.openjfx:javafx-base:${jfxVersion}:${jfxPlatform}"
    compileAndRuntime "org.openjfx:javafx-graphics:${jfxVersion}:${jfxPlatform}"
    compileAndRuntime "org.openjfx:javafx-controls:${jfxVersion}:${jfxPlatform}"
    compileAndRuntime "org.openjfx:javafx-media:${jfxVersion}:${jfxPlatform}"
    compileAndRuntime "org.openjfx:javafx-fxml:${jfxVersion}:${jfxPlatform}"
    compileAndRuntime "org.openjfx:javafx-web:${jfxVersion}:${jfxPlatform}"
}

private static def getJavaFxPlatform() {
    def os = System.getProperty('os.name').toLowerCase(Locale.ENGLISH)
    if (os.contains('win')) {
        return 'win'
    }
    if (os.contains('nix') || os.contains('nux')) {
        return 'linux'
    }
    if (os.contains('osx') || os.contains('mac')) {
        return 'mac'
    }
    assert false: "unknown os: $os"
}
```

For any dependency you want to be included in the final `fatJar` you will need to use the `compileAndRuntime` dependency type. Additionally if you want to include the native binaries for more than one platform, you need to declare the same dependency multiple times, but with each modifier (`win`/`linux`/`mac` for JavaFX 16, though later versions may change these classifiers)

## 4: Create the `fatJar` task

Gradle has a variety of different `fatJar`/`uberJar` plugins available. A lot of them break between versions so we'll create our own that we know works with the current version _(Gradle 7 at the time of writing)_

We want to create a manifest file that specifies the main class and bundles all of our dependencies. As mentioned before we need a custom configuration to support fetching dependencies. Common `fatJar` tasks found online will be our base, and we just replace their usage of `configurations.blablabla` with our custom one, `configurations.compileAndRuntime`.
```groovy
task fatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': mainClassName
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from { configurations.compileAndRuntime.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}
```

Once you have the class name in there, you're done.

## Building & running the jar

Open a terminal window and run `gradlew fatJar`. This will compile the project and place the contents into a new folder named `builds`.
The fat-jar is located in the `libs` folder.

You can run the jar file via `java -jar <path-to-jar-file>`.

JavaFX artifacts are released as Java 11 binaries. So you'll need to be using Java 11 or later to run the application. 

If you don't want to have to run `gradlew fatJar` and are using an IDE, different IDE's will integrate with Gradle in different ways to provide UI access to Gradle features. For example, in IntelliJ if you open the project by selecting the [`build.gradle`](build.gradle) or the directory that the [`build.gradle`](build.gradle) is in then it should recognize it as a Gradle project. When IntelliJ has a Gradle project open it should provide a `Gradle` tab on the right. Clicking on it will expand it, and from there you can see all the options and functions it provides. The custom `fatJar` task should appear under the _"other"_ category.