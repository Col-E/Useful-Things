package org.example.weather;

import org.example.weather.impl.*;
import org.int4.dirk.api.Injector;
import org.int4.dirk.jsr330.Injectors;

import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		// Configure dirk injector
		//  - there are different 'Injectors' classes per each style (jsr-330 being used here)
		Injector injector = Injectors.autoDiscovering();
		injector.register(Arrays.asList(
				// Specify implementations
				CommandLoader.class,
				CommandManager.class,
				LocationProvider.class,
				NetworkAccessor.class,
				WeatherProvider.class,
				// And the application type
				WeatherApplication.class));
		// Create app instance
		WeatherApplication application = injector.getInstance(WeatherApplication.class);
		// Run app
		application.run();
	}
}
