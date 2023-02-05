# Dependency Injection

Dependency Injection frameworks automate the dependency injection design, allowing you to more cleanly organize your code with less manual passing around of dozens of dependency instances.

- Video summary: [_"What is dependency injection?"_](https://www.youtube.com/watch?v=ZZ_qek0hGkM)
- Sample application examples: [Weather application](../tutorials/dependency-injection)

## Feather

- Site: https://github.com/zsoltherpai/feather
- Maven: https://mvnrepository.com/artifact/org.codejargon.feather/feather

Feather is an ultra-lightweight dependency injection. Its an ideal solution if you want a very simple injection system without any bloat.

**Example**

```java
// Instantiate the injector
Feather feather = Feather.with(... /* module instances go here */);

// Declare modules to provide instances with JSR-330 annotations
public class DataModule {
    @Provides
    @Singleton
    DataSource ds() {
        return ...;
    }
}

// Declare your class with a constructor annotated with @Inject and the dependencies it uses
public class DataConsumer {
    @Inject 
    public DataConsumer(DataSource ds) {
        // ...
    }
}

// Create the class instance
DataConsumer consumer = feather.instance(DataConsumer.class);
```

## Dirk

- Site: https://github.com/hjohn/Dirk
- Maven: https://mvnrepository.com/artifact/org.int4.dirk

Dirk is a flexible lightweight dependency injection. It offers multiple implementations to support differing frameworks (old `javax.inject` vs `jakarta.inject` for example).

**Example**

```java
// Instantiate the injector
Injector injector = Injectors.autoDiscovering()
injector.register(Arrays.asList(... /* implementation types go here */));

// Declare your class with a constructor annotated with @Inject and the dependencies it uses
public class DataConsumer {
    @Inject 
    public DataConsumer(DataSource ds) {
        // ...
    }
}

// Create the class instance
DataConsumer consumer = injector.getInstance(DataConsumer.class);
```

## Spring Boot

- Site: https://spring.io/projects/spring-boot/
- Maven: https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter

Spring boot is an all-in-one bundle for creating modular applications. It also has CDI support.

**Example**

```java
// Declare your class with a constructor annotated with @Inject and the dependencies it uses
@Component
public class DataConsumer {
    @Inject 
    public DataConsumer(DataSource ds) {
        // ...
    }
}

// Use automated scanning from Spring (v3+ also supports JSR-330 annotations)
@SpringBootApplication(scanBasePackages = ...)
public class App {
	public static void main(String[] args) {
		// Pull spring context info from annotation
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringMain.class);
		// Create the class instance
		DataConsumer consumer = context.getBean(DataConsumer.class)
	}
}
```

## Weld

- Site: https://weld.cdi-spec.org/
- Maven: https://mvnrepository.com/artifact/org.jboss.weld.se/weld-se-core

Weld is the most _"enterprise"_ item on this list, but it offers flexible and convinent dependency injection. 
Version 3X will use `javax.inject` and 4X and above will use `jakarta.inject`.

**Example**

```java
// Declare your class with a constructor annotated with @Inject and the dependencies it uses
public class DataConsumer {
    @Inject 
    public DataConsumer(DataSource ds) {
        // ...
    }
}

// Setup weld container, recursively scan for injectable components from a class's package
// Can do either:
//  - Weld.newInstance() - From weld
//  - SeContainerInitializer.newInstance() - From CDI spec
SeContainer container = SeContainerInitializer.newInstance()
		.addPackages(true, DataConsumer.class)
		.initialize();
// Create the class instance
DataConsumer consumer = container.select(DataConsumer.class).get();
```

## Guice

- Site: https://github.com/google/guice
- Maven: https://mvnrepository.com/artifact/com.google.inject/guice

Guice is a more configurable dependency injection framework with more advanced features _(All covered quite well on its [wiki page](https://github.com/google/guice/wiki))_.

**Example**

```java
// Instantiate the injector
Injector injector = Guice.createInjector((... /* module instances go here */);

// Declare modules to bind base types to implementations
public class DataModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataModule.class).to(DataModuleImpl.class);
    }
}

// Declare your class with a constructor annotated with @Inject and the dependencies it uses
public class DataConsumer {
    @Inject 
    public DataConsumer(DataSource ds) {
        // ...
    }
}

// Create the class instance
DataConsumer consumer = injector.getInstance(DataConsumer.class);
```

## Dagger

- Site: https://github.com/google/dagger
- Maven: https://mvnrepository.com/artifact/com.google.dagger/dagger

Dagger similar to Guice except instead of doing things at runtime, it generates all the dependency injection code at compile time
_(Meaning you can even debug it if you wanted to)_. It adds some new items to standard dependency injection logic to allow this. For example, its component system.

```java
// Declare modules to provide instances with JSR-330 annotations
@Module
public class DataModule {
    @Provides
    @Singleton
    DataSource provideDataSource() {
        return ...;
    }
}

// Declare your class with a constructor annotated with @Inject and the dependencies it uses
public class DataConsumer {
    @Inject 
    public DataConsumer(DataSource ds) {
        // ...
    }
}

// Declare components to utilize multiple modules 
// and optionally allow static injection into a given type (IE: MyExample)
@Singleton
@Component(modules = { DataModule.class })
public interface ExampleComponent {
    DataConsumer createConsumer(); // create types with injection dependencies

    void inject(MyExample example); // inject into a type with annotated fields
}

// Create a consumer instance
DaggerExampleComponent component = DaggerExampleComponent.builder().build();
DataConsumer consumer = component.createConsumer();

// Example class that uses the component to inject into itself, populating annotated field instances
class MyExample {
	@Inject
	DataConsumer consumer;

	MyExample() {
        // Create the component after generating the dependency injection code
    	DaggerExampleComponent component = DaggerExampleComponent.builder().build();
    	component.inject(this);
	}
}
```