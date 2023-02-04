package org.example.weather;

import io.avaje.inject.BeanScope;

public class Main {
	public static void main(String[] args) {
		// Using BeanScope, the generated module in 'core-avaje' will be found
		BeanScope beanScope = BeanScope.builder().build();
		// Create app instance
		WeatherApplication application = beanScope.get(WeatherApplication.class);
		// Run app
		application.run();
	}
}
