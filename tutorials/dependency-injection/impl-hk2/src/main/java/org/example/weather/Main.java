package org.example.weather;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

public class Main {
	public static void main(String[] args) {
		// Load from the locator entry 'META-INF/hk2-locator/default' created by 'hk2-metadata-generator'
		ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
		// Create the weather application instance and run it.
		WeatherApplication application = locator.create(WeatherApplication.class);
		application.run();
	}
}
