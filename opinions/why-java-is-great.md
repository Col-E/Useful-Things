# Why Java is Great

I'll cut to the chase. Here's the TLDR:

 - The syntax is a conservative derivative of C:
     - Easy to read coming from any other C family language
     - Strongly typed means no guessing the type of even poorly named variables, unlike duck-typed languages
 - The ecosystem is one of the largest out there:
    - There is a library for everything.
    - There is an answer to almost every problem on StackOverflow, even for 3rd party libraries
 - The tooling is unrivaled:
    - The build tools _(Maven and Gradle)_ are both excellent when compared to other languages build tools _(assuming they have any)_
    - The IDE's automate most of the grunt work away
 - The multi-platform nature of the JVM:
    - You _"write once, run anywhere"_	 
 - The speed of the JVM:
    - Java's performance is all in the long-game due to the nature of JIT. But if you want a short-lived program that opens quickly there are AOT options too.
 - The language is constantly evolving:
 	- New language features allow more elegant syntax as pattern matching capabilities are added
 	- Performance enhancements are on the horizon _(Valhalla)_

## The Syntax

I said Java follows the C style conservatively, but what does that mean? C styled languages use curly braces `{}` to form blocks of functionality. They also provide programming fundamentals such as variables, assignments and expressions, functions, parameters, and control flow. All of these are expressed in a way visually similar to that of C. If you learn one C styled language it is very easy to jump to another. Since the concepts and general syntax are shared all you need to focus on is learning about the ecosystem surrounding the language.

### Similarity among C styled languages

Here is a basic Fibonacci sequence calculator in Java:
```java
int F(int n) {
    if (n == 0 || n == 1)
        return n;
    else
        return F(n - 1) + F(n - 2);
}
```
Now lets compare that to Python. Its loosely C styled. It has most of the same programming fundamentals. This simple case is obviously a mirror image of the former Java code. Though we do lose some things like curly braces and type information. There is no explicit return type, and the parameter `n` is also no longer typed. Python is a dynamically typed language. For a simple case like this that is a non-issue. 
```python
def F(n): 
    if n == 0 || n == 1:
        return n
    else:
        return F(n - 1) + F(n - 2)
```

### Strong typing

So when does a lack of strong typing become an issue? Consider this hypothetical situation; you are a new hire in some company and are tasked with fixing a bug in a sizable Python application. There are lots of custom data modeling types, utility methods spanning across a few files, and the prior developers were not the best about naming their variables and function names. Since you aren't the original author of the code you don't have any insight into the intended types of variables and function returns. This sort of data can be learned, but you need to follow the control flow of the application and work backwards for anything not obvious like an array, string, or number. Those modeling types? Lets say there is a function named `def mapRawToModel(raw)`. What is the return type of this function? It should be a model based on the name, but what kind of model? What fields and functions does it have? Heck, if the last person on the project was incompetent maybe they changed the return value of the method to something that isn't a model and didn't bother to rename the function. 

Point being, if you enforce strong static typing you can just look and see the function return type. This prevents both of the pain-points mentioned before:

- No needing to work backwards from a location with a known type, function return types are given.
- No needing to sanity check a duck-typed return type. If somebody needs to change the type of a method, the method definition must also change. This also makes it clear when a change is done so but associated documentation is not updated.

Outside of general legibility, there are functionality implications one must consider with dynamically typed languages. Because types are only handled at runtime, improper usage of a type is only ever going to fail at runtime. In a strongly typed language passing the wrong value type to a function, calling a non-existent function on a type, etc. will all fail at compile time. And with an IDE you typically will be warned before even needing to compile once about the incorrect usage.

## The Ecosystem

### Libraries and Community Q&A

Java has been around for quite some time and has amassed a large following. Its used by the enterprise world and hobbyists alike. With the diverse set of users that Java has, there is basically a library for anything you could want. As a branching off point you can take a gander at [awesome-java](https://github.com/akullpp/awesome-java), a list of popular libraries for a assorted set of categories. From there you can use services like [LibHunt](https://www.libhunt.com/) to find other similar libraries.

In addition to the plethora of libraries, there is also an answer to every almost every stack overflow problem you could imagine. It doesn't matter if the question is about the core language and classes provided in the JDK, or if its about a third party library. Most popular libraries have hundreds or sometimes thousands of questions related to usage and bugs/crashes.

### Build Tools

There may have been others in the past, like [ANT](https://ant.apache.org/), but currently there are two major build tools that everyone uses; [Maven](https://maven.apache.org/) and [Gradle](https://gradle.org/). Both manage the complete build pipeline, from dependency management, managing modular project structures, compiling source code, pre and post-processing source and compiled code, packaging the compiled code into a release, and handling publishing of releases. It all can be done through a single config file and a few short commands for either build tool. 

Maven's config uses an XML file called `pom.xml`. It follows a consistent structure so you know what to expect when opening one up or creating a new one. There is no such thing as undefined behavior since unrecognized tags and sections are ignored, and malformed values within recognized tags and sections yield build failures. Custom logic can be added to a Maven toolchain through custom plugins. 

Gradle on the other hand uses a Groovy source code file called `build.gradle`. Since the file is made with Groovy you can write basic Java/Groovy code to declare your workflow and even custom tasks without having to create plugins. The Groovy syntax results in needing less characters to do similar things than in a Maven `pom.xml` file, such as one-liner dependency declarations. Though with such freedom comes a responsibility to keep your `build.gradle` clean.

> Note: Maven is developed by the Apache software foundation and follows strict rules in creating releases. This is opposed to Gradle's _'Move fast and break things'_ approach which gives it many more new features faster than Maven, at the cost of occasionally breaking builds when updating to newer versions.

### IDEs - Primarily IntelliJ

[Eclipse](https://eclipseide.org/release/) is cool and all, but I'll be using [IntelliJ](https://www.jetbrains.com/idea/) for this section. Its just the most polished of the common Java IDEs.

So why is an IDE like IntelliJ great?

- Makes navigating larger projects a breeze
    - A file tree of the project is given to you with specialized icons for different kinds of files _(Making skimming over things visually quite easy)_
    - Quick-navigation where you can type in a class/member name and a list of matching items is shown in a keyboard navigable list. Pressing enter opens that item in an editor tab.
    - Project wide text search with configurable search parameters _(Such as file type masks, modes for regex, word-only, case-sensitivity, and more)_. The current selected item is previewed in a small window, and that window is actually editable. So small changes can be made quickly within this small text search prompt without having to open the full file.

- Integration with build tools like Maven / Gradle
    - Auto-import dependencies from build tool configuration
    - Handy access to run different build tool processes from the UI
    - Display build tool test results in the UI in an intuitive way _(Can filter to show only failing tests, re-run only those tests with a single click)_
- Visualize compiler errors and warnings without having to compile
    - You passed the wrong type to a method? Here's some red squiggles.
    - You call a method on a value that is most likely going to be `null` at runtime? Here's some yellow squiggles telling you why your value is probably `null` and this method call will explode in your face.
    - You have some duplicate code? Here's a button to refactor it for you.
    - You using a known inefficient pattern? Here's a button to replace it with a faster one.
    - You using something that can be simplified using new Java language features? Yup, here's a button to use the new feature and simplify your code.
- Auto-generate boilerplate and general auto-completion
    - People say _"Java has so much boilerplate"_, I say _"And I generate it all with one keybind, and my IDE can update it automatically later if it needs to be changed"_
    - Tab completion is top notch, I probably only write the first three characters or so on average before completing the suggestions. And they're not always simple ones either. IntelliJ's smart completion can finish complete chained method calls to satisfy a variable type, or complete a series of stream operations.
    - IntelliJ's auto-generate menu lets you generate constructors, getters, setters, toString/hashCode/equals implementations, override methods from parent types, and a few more common patterns.
    - There are super useful shortcuts for generating variables from expressions, and different for-each pattern loops on iterable expressions. Existing code of expressions when generating variables can also be inlined to use the newly made variable declaration. 
- Refactoring
    - Code is rarely made perfect the first time around. IntelliJ's refactoring menus are incredibly useful. Rename classes and members, move members to different classes, extract inner classes to their own files, and more. If I want to re-organize the structure of my application its incredibly easy to rename things and move them around with all the side effects managed by IntelliJ.
- Other useful integrations and tools
	- Need a git client? Its included.
	- Need a database manager and editor? Included as well.
	- Need a profiler to analyze where your code is bottlenecking? Also included.
- And more. Just skim over the [features page](https://www.jetbrains.com/idea/features/)

## The Platform & Time Independence

Java has an old motto of _"write once, run anywhere"_. If you consider a language like C once you compile a program with it the program will only run on the platform it was compiled on. If you compile on an intel x86 machine, you won't be able to run on an ARM machine. 

But of course there's more than instruction sets. Then there's also operating system behaviors. The implications of this are more obvious when you have something like a graphical user interface. QT is a popular library that provides a _"cross platform"_ UI. It is indeed cross platform, but at the source level. Due to things like static linking, different tie-ins to operating specific rendering pipelines, and deployment strategies you'll find that the process for using QT in a Windows vs Linux context varies greatly at the compilation step and for distribution.

So how does Java's _"write once run anywhere"_ come into play? Well, that's where bytecode comes in. When you compile a Java program it creates `.class` files. Elaborating on that, if you have Java installed you can run basically any Java application compiled from that version or older regardless of the platform it was compiled on. It is completely possible to run a Java program written in 2003 on modern Java releases.  But even on modern hardware it still runs. Why? Because Java translates bytecode to native platform specific machine code on the fly. It does so with a technology called [JIT](https://developers.redhat.com/articles/2021/06/23/how-jit-compiler-boosts-java-performance-openjdk) _(Just In Time - for compilation)_. Since the format Java applications are distributed in _(class files)_ are an intermediate representation for application logic, the applications are platform agnostic. Each platform has its own Java virtual machine that translates the bytecode to machine code when the application is actually executed.

Now regarding time independence, the bytecode format does evolve over time to support new features. But it does so with backwards compatibility in mind. New features in the source language of Java can be implemented as syntatic sugar in the compiler, or whole new attributes specified in the class file specification. Nothing that already exists within the class file specification can be changed, only new content added. Thus you have the guarantee that old Java software will run on your machine even with newer versions of Java installed.

## The Performance

_"Wouldn't having an intermediate step slow things down"_ is something a lot of people probably think when first exposed to this idea. Especially since the code is being compiled as you run the software. Let's break this down a bit before answering this question.

Most of the code in your Java application is executed only a few times, and in the case of bundling libraries there's lots of logic that will go entirely unused. Spending time creating machine code for things rarely used would be a waste of CPU time and resources. So in the case of HotSpot _(What you'll use in OpenJDK builds and similar families)_ code like this _doesn't_ get compiled. It gets interpreted. This is fine if our assumption that the code being run is only done so a few times. But interpretation isn't as fast as machine code. So what happens when we need to run a method often? The HotSpot VM will notice cases of _"hot"_ methods via its profiler and then use the JIT compiler to generate machine code for those methods. But it isn't that straight-forward either. 

Jumping back to C again for just a moment, when you compile C code you tell the compiler what level of optimization you want to build. `-O1` will do some basic optimizations _(as to not reduce compile time drastically)_ with the goals of reducing file size and increasing performance. Things like guessing branch jumps to save CPU cycles, and deferring stack pops for function arguments to do them in chunks. `-O2` enables a few more optimizations so long as they do not affect file size and increases compile time slightly. But `-O3` does more to focus on performance at the cost of file size with optimizations such as loop unrolling and function in-lining, though this level of optimization takes even longer to compile. The key here though is that these are optimizations made by looking at patterns in the generated code and replacing them with alternative patterns that are faster, and more advanced optimizations take longer to compute. How does the JIT tie into this? Well, it will do something called _"tiered compilation"_ in which a method called often will _in essence_ start being compiled with -`O1` then after being used even more often will transition to `-O2/3`. I say _"in essence"_ because it is not the same as the GCC optimization flags, but the idea is close enough. It begins with optimizations that are quick to compute. Then for very frequent usage of methods it will further optimize that code. And in that last phase we encounter another benefit of the JIT approach that compile-time optimization does not have. Rewriting code based on runtime behavior. 

Consider guessing the outcome of branch jumps. What if instead of making a guess based off of surrounding code, you could run the method a dozen times, then use the actual statistics of branch-taken vs branch-not-taken to generate the correct optimized code that assumes the proper outcome. And beyond that, if for some reason a method were to begin with branch-taken as the preferred route, but later during runtime switch to branch-not-taken, the profiler would notice this and have the JIT replace the method code with new logic to accommodate the new _"common"_ case. In a similar vein you could use this train of thought to skip some code like array bounds and `null` checks. If you see that a `null` check always shows that the passed value is not `null` you can rewrite the method to skip checking and immediately assume the value is there. Then if the value later is `null` for some reason revert back to having the `null` check exist. 

So now lets answer that question, _"Wouldn't having an intermediate step slow things down?"_ 

Well, yes and no. Yes, because at first we are interpreting the bytecode, which is obviously slower than native code. But as mentioned before, the only situation in which code is interpreted is when the code isn't used often. So the loss there is negligible. What about the common code then? Well, again we can start off with basic optimizations that are quick to compute. And we're only doing this on a per-method basis. So unlike our quick example with C, we are compiling a very small fraction of the application any time we need to do this step. Even if we transition to being a _"hot"_ method and use our most advanced forms of optimization once we've compiled the method thats it. At least until the JVM profiler detects circumstances would be better off with a different set of optimizations. But while we are in this final state we are equal to or faster than equivalent statically compiled code from a compiler such as GCC. The trade off we have is that while our final optimizations may be better it does take a short amount of time to enter this state. To answer the question, it actually depends on the kind of application we're creating.

For an application that only starts up and does one thing then closes, this is not an ideal setup. Our optimization benefits from long term analysis of runtime behavior. For an application that starts up and stays open for more than a few minutes that is when the trade offs really become apparent. Server software for instance is an ideal candidate for this approach to creating optimized code since they typically stay alive for hours or even days and weeks.

But even if an application is to be short lived you can still stick with Java because there are tools like [GraalVM](https://www.graalvm.org/)'s [Native Image](https://www.graalvm.org/native-image/) program, which allows you to create ahead-of-time _(AOT)_ compiled images of your application for a given platform, just like if you were to compile a C program. They will of course not have the benefits of JIT compilation, which means you'll want to consider how long the application will be alive before adopting this approach. Another double edged sword of AOT compiled applications is that dynamic capabilities of Java are a bit finicky to work with, [though it is possible with the right configuration](https://blog.frankel.ch/configuring-graal-native-aot-reflection/).

## The Language Pushes Forward

Java is an old language, but don't let its age fool you. Its evolving faster, now more than ever, with new features every release and massive overhauls to the JVM internals for some wild performance enhancements. For this I'm just gonna dump some resources on you so you can see for yourself:

### Performance

- [Java's new G1 garbage collector slowly being improved with new releases](https://kstefanj.github.io/2021/11/24/gc-progress-8-17.html)
- [Valhalla performance metrics](https://youtu.be/1H4vmT-Va4o?t=915) - Timestamped at Valhalla's performance examples
- [Project Liliput - Reducing Object header size](https://youtu.be/KuHhUDhIFYs?t=482) - Timestamped when Nicolai discusses the intent of Liliput

### Features

- [Categorized list of all Java and JVM features since JDK 8 to 18](https://advancedweb.hu/a-categorized-list-of-all-java-and-jvm-features-since-jdk-8-to-18/) - Includes breakdown of which features were added when and example code for new language features.
