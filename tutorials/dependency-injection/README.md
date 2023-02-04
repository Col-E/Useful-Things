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

- `Core` holds the demo project, a weather forecast command line application. All of the interfaces and implementing types are annotated using JSR-330 annotations.

**Implementations**

- [`Impl-Dagger`](impl-dagger) - Using _[Dagger](https://github.com/google/dagger)_ framework to generate code for DI.
- [`Impl-Dirk`](impl-dirk) - Using _[Dirk](https://github.com/hjohn/Dirk)_ to reflectively populate injected instances.
- [`Impl-Feather`](impl-feather) - Using _[Feather](https://github.com/zsoltherpai/feather)_ to reflectively populate injected instances.
- [`Impl-Guice`](impl-guice) - Using _[Guice](https://github.com/google/guice)_ to reflectively populate injected instances.
- [`Impl-JayWire`](impl-jaywire) - Using _[JayWire](https://github.com/vanillasource/jaywire)_ to manually populate injected instances.
- [`Impl-Spring`](impl-spring) - Using _[Spring Boot](https://spring.io/projects/spring-boot/)_ to reflectively populate injected instances.
- [`Impl-Weld`](impl-weld) - Using _[Weld](https://weld.cdi-spec.org/)_ to reflectively populate injected instances.