package org.example.weather;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

public class Main {
	public static void main(String[] args) {
		// Setup weld container, recursively scan for injectable components from the base weather package.
		// Can do either:
		//  - Weld.newInstance()
		//  - SeContainerInitializer.newInstance()
		SeContainer container = SeContainerInitializer.newInstance()
				.addPackages(true, WeatherApplication.class)
				.initialize();
		// Create application instance and run
		WeatherApplication application = container.select(WeatherApplication.class).get();
		application.run();
	}
}
