# Setting up `fatJar` JavaFX with Maven

> If you don't already know what Maven is, check out this [introduction post](https://www.baeldung.com/maven).

To keep it short and simple, all you need to do is two things:

1. Add the JavaFX dependencies to the `<dependencies>` block
2. Add the `maven-assembly-plugin` plugin, and configure it to generate a `jar-with-dependencies`

Now we can go into more detail.

## 1: Adding the JavaFX dependencies

For looking up Maven artifacts, I prefer `mvnrepository.com` over the official `maven.org` since it makes it much easier to quickly find what I want and copy and paste it into my projects. 
With this in mind, to get the JavaFX dependencies you can find them all located here: [mvnrepository.com/artifact/org.openjfx](https://mvnrepository.com/artifact/org.openjfx)

When you click on an artifact it will list all the available versions for it. Usually you're safe to select the most up-to-date version. 
Once selected it will show you a series of tabs for `Maven`, `Gradle`, and a few others. Make sure `Maven` is selected and copy the `XML` text provided.

Now switch over to your project and open the [`pom.xml`](pom.xml). Find the `<dependencies>` block, or create one if it does not exist. Then paste it in.

> What if I want to target Java 11 or higher?

Then you do not need to do anything extra for this step.

> What if I want to target Java 8?

If you want to target Java 8, you will need to ensure the JVM you will be running on bundles JavaFX. 
Some JVM's that bundle JavaFX include: 

- [Amazon's Corretto](https://aws.amazon.com/corretto/)
- [Azul's ZuluFX](https://www.azul.com/downloads/?package=jdk-fx)
- [Bellsoft's Liberica](https://bell-sw.com/pages/downloads/) _(Full JDK package)_

Then if you want to reduce the size if the jar file you generate you can add `<scope>provided</scope>` to the JavaFX dependencies.
This will let you compile against the JavaFX classes, but they will be excluded from the `package` process by the `maven-assembly-plugin`.
When you run the `jar-with-dependencies` it will depend on whatever JavaFX version is bundled with the current JVM.

## 2: Adding the `maven-assembly-plugin` and configuring it

The `maven-assembly-plugin` is main plugin you'll want to use for building far-jars. There are other options, but this one is the simplest in my opinion.

To add the plugin, find your `<build>` block _(or create one)_, then inside that find `<plugins>` _(again, create if needed)_, and then put it in there. The plugin XML text may look a bit big and scary at first, but you won't actually need to modify much of it. Its quite easy to copy/paste it between projects and change what you need to.

If you check and see what is present in the [`pom.xml`](pom.xml) most of it is just the bare minimum to get it configured to build a fat-jar.
In most cases the only line you will need to edit between projects is the `mainClass` line, which points to the class that defines `public static void main(String[] args)`.

Once you have the class name in there, you're done.

> **Note**: Due to [name path overlaps](https://x.com/invokecoley/status/1511573194049835009) between different architectures for a given platform _(such as win-x86_64 & win-ARM)_ only one of the architectures will be supported at a time.

## Building & running the jar

Open a terminal window and run `mvn package`. This will compile the project and place the contents into a new folder named `target`.
The fat-jar is the file with the `jar-with-dependencies` in the name. The smaller jar without this piece in its name is your project's code without any dependencies included.

You can run the larger `jar-with-dependencies` via `java -jar <path-to-jar-file>`.

This project has been configured to run with Java 16, so you will need to ensure you use Java 16 or later to run the generated jar. 
Of course, you can also change the configuration of this demo project to target a different version of Java if you want to.

If you don't want to have to run `mvn package` and are using an IDE, different IDE's will integrate with Mavenin different ways to provide UI access to Maven features. For example, in IntelliJ if you open the project by selecting the [`pom.xml`](pom.xml) or the directory that the [`pom.xml`](pom.xml) is in then it should recognize it as a Maven project. When IntelliJ has a Maven project open it should provide a `Maven` tab on the right. Clicking on it will expand it, and from there you can see all the options and functions it provides. It should be relatively self-explanatory _(And if it is not, I encourage you to try out each option and see what they do)_.