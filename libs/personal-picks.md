# Personal Picks: Good Java Libraries

## Build

- [Maven](https://maven.apache.org/) - Uses XML configuration for declaring project structure, dependencies, and plugins. Not the prettiest, but the most straight forward of the systems for the most part.
- [Gradle](https://gradle.org/) - Uses Groovy to create a build _script_ with configuration-like declarations. Incredibly flexible, but potentially to your own detriment. Easy to overcomplicate builds, and the plugin system deprecates dozens of features every release so updating often breaks your builds.
- [Saker.build](https://saker.build/) - An interesting alternative to Gradle that supports more than just Java projects.

## Bytecode

- [ASM](https://asm.ow2.io/) - Arguably the best Java bytecode library out there currently. Its fast, abstracts away a lot of tedious aspects of the class file format, and is extremely well maintained.
- [ByteBuddy](https://bytebuddy.net/#/) - Code generation / manipulation library built on top of ASM. Simplifies usage of ASM by a significant degree.

## Command Line

- [picocli](https://picocli.info/) - *"A mighty tiny CLI"* is indeed a well-deserved description. Add a few annotations to classes representing your command line flags and arguments and the rest is done automatically for you.

## Collections

- Primitives - Honestly, any of them work. They all offer primitive collection implementations.
    - [Fastutil](https://github.com/vigna/fastutil)
    - [High Performance Primitive Collections](https://github.com/carrotsearch/hppc)

## Dependency Injection & CDI

- [Feather](https://github.com/zsoltherpai/feather) - Ultra-lightweight JSR-330 complaint library.
- [Dirk](https://github.com/hjohn/Dirk) - Lightweight and customizable DI framework supporting JSR-330, Jakarta-Inject, and CDI. Documentation in main readme is top-notch.
- [Weld](https://weld.cdi-spec.org/) - Moving beyond DI, CDI offers a set of additional useful features. Weld is the CDI reference implementation.

## Events

- [ssbus](https://github.com/xxDark/ssbus) - Performance oriented event bus using runtime class generation.

## Introspection / Reflection

- [deencapsulation](https://github.com/xxDark/deencapsulation) & [Venuzdonoa](https://github.com/xxDark/Venuzdonoa) - Don't like Java 9+ modules? Just disable the module system then!
- [Objenesis](https://github.com/easymock/objenesis) - Allow instantiation of any object type by bypassing the constructor. Can be quite useful in some circumstances.

## Json

- [Gson](https://github.com/google/gson) - The most popular Json library. API is stupid simple and is easily configured, though most situations should work out of the box.

## Serialization

- [Fury](https://github.com/apache/fury) - Automatic binary serialization that is incredibly well optimized in size and read/write speeds.