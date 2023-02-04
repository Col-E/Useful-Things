package org.example.weather;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;

import javax.inject.Singleton;

@Configuration
@ComponentScan(basePackages = "org.example.weather", includeFilters = @Filter(Singleton.class))
public class Main {
	public static void main(String[] args) {
		// Create the spring context, which will pull config from the annotations on this class.
		// Using the 'ComponentScan' we can auto-discover implementations for our interfaces.
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class)) {
			context.getBean(WeatherApplication.class).run();
		}
	}
}
