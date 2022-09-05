package org.example.weather;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.example.weather")
public class SpringMain {
	public static void main(String[] args) {
		// Create the spring context, which will pull config from the annotations on this class.
		// Using the 'ComponentScan' we can auto-discover implementations for our interfaces.
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringMain.class);
		// WeatherApplication is 'Runnable'
		//
		// Normally you'd just have the application class itself here (WeatherApplication.class)
		// but we cannot due to some hacks mentioned beforehand.
		//
		// Also, alternatively you'd be able to use the single annotation '@SpringBootApplication'
		// to handle basically everything for you (no need to create a context object)
		context.getBean(Runnable.class).run();
	}
}
