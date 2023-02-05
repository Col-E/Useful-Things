# Dependency Injection Demo Project

This is a multi-module maven project demonstrating how to use a variety of different DI frameworks by utilizing JSR-330 annotations.

## Usage

Run one of the `Main` classes in one of the `impl` modules.

```
>help

Available commands:
 - current
 - remote
 - where

>where

38, -77

>current

Temperature: 83.00'F
Forecast:    Chance Showers And Thunderstorms
Wind:        South 7.00mph
```

## Modules

**Core: JSR-330**:

- `Core` holds the demo project, a weather forecast command line application.
- `Core-Avaje` is a modified version of `Core` that specifies the `avaje-inject-generator` processor in the `pom.xml`.
- `Core-JBock` is a modified version of `Core` that specifies the `simple-component-compiler` processor in the `pom.xml`.

**Core: Custom annotations**:

- `Core-HK2` is a modified version of `Core` that specifies the `hk2-metadata-generator` processor in the `pom.xml`, and uses `@Service`/`@Contract` from HK2.
- `Core-Inverno` is a modified version of `Core` that specifies the `inverno-core-compiler` processor in the `pom.xml`, and uses `@Bean` from Inverno.

**Implementations**

- [`Impl-Avaje`](impl-avaje) - Using [Avaje-Inject](https://github.com/avaje/avaje-inject) framework to generate code for DI.
- [`Impl-Dagger`](impl-dagger) - Using _[Dagger](https://github.com/google/dagger)_ framework to generate code for DI.
- [`Impl-Dirk`](impl-dirk) - Using _[Dirk](https://github.com/hjohn/Dirk)_ to reflectively populate injected instances.
- [`Impl-Feather`](impl-feather) - Using _[Feather](https://github.com/zsoltherpai/feather)_ to reflectively populate injected instances.
- [`Impl-Guice`](impl-guice) - Using _[Guice](https://github.com/google/guice)_ to reflectively populate injected instances.
- [`Impl-HK2`](impl-hk2) - Using _[Glassfish HK2](https://github.com/eclipse-ee4j/glassfish-hk2)_ to use a service-loader to discover and inject instances.
- [`Impl-Inverno`](impl-inverno) - Using _[Inverno](https://github.com/inverno-io/inverno-core)_ framework to generate code for DI from a `module-info` file.
- [`Impl-JayWire`](impl-jaywire) - Using _[JayWire](https://github.com/vanillasource/jaywire)_ to manually populate injected instances.
- [`Impl-JBock`](impl-jbock) - Using _[JBock](https://github.com/jbock-java/simple-component)_ to generate code for DI, with mostly pre-defined existing module implementation.
- [`Impl-Spring`](impl-spring) - Using _[Spring Boot](https://spring.io/projects/spring-boot/)_ to reflectively populate injected instances.
- [`Impl-Weld`](impl-weld) - Using _[Weld](https://weld.cdi-spec.org/)_ to reflectively populate injected instances.