package org.example.weather;

import org.codejargon.feather.Feather;

public class Main {
	public static void main(String[] args) {
		// Configure feather injector
		Feather injector = Feather.with(new WeatherModule());
		// Create app instance
		WeatherApplication application = injector.instance(WeatherApplication.class);
		// Run app
		application.run();
	}
}
