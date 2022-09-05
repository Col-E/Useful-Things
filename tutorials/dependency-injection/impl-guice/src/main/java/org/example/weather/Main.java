package org.example.weather;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
	public static void main(String[] args) {
		// Configure guice injector
		Injector injector = Guice.createInjector(new WeatherModule());
		// Create app instance
		WeatherApplication application = injector.getInstance(WeatherApplication.class);
		// Run app
		application.run();
	}
}
