package org.example.weather;

import org.jboss.weld.environment.se.Weld;

import javax.enterprise.inject.se.SeContainer;

public class Main {
	public static void main(String[] args) {
		// Setup weld container, recursively scan for injectable components from the base weather package
		SeContainer container = Weld.newInstance()
				.addPackages(true, WeatherApplication.class)
				.initialize();
		// Create application instance and run
		WeatherApplication application = container.select(WeatherApplication.class).get();
		application.run();
	}
}
