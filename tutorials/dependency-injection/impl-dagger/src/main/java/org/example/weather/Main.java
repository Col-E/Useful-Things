package org.example.weather;

public class Main {
	public static void main(String[] args) {
		// Configure dagger component
		//  - Requires a project build to generate the 'DaggerWeatherComponent' type
		WeatherComponent component = DaggerWeatherComponent.create();
		// Create app instance
		WeatherApplication application = component.createApplication();
		// Run app
		application.run();
	}
}
