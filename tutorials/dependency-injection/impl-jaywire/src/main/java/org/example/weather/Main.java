package org.example.weather;

public class Main {
	public static void main(String[] args) {
		// Configure JayWire module
		WeatherModule module = new WeatherModule();
		// Create app instance
		WeatherApplication application = module.application();
		// Run app
		application.run();
	}
}
