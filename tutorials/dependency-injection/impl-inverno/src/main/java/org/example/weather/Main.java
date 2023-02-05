package org.example.weather;

public class Main {
	public static void main(String[] args) {
		// Build inverno module implementation, then activate it with 'start()'
		//  - Requires a project build to generate the 'Weather' and 'Weather.Builder' type
		Weather weather = new Weather.Builder().doBuild();
		weather.start();
		// Run the application
		WeatherApplication application = weather.weatherApplication();
		application.run();
	}
}
