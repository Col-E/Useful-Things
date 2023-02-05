package org.example.weather.commands;

import org.example.weather.WeatherApplication;
import org.example.weather.data.LocationLookupException;
import org.example.weather.data.Weather;
import org.example.weather.data.WeatherLookupException;
import org.example.weather.interfaces.ILocationProvider;
import org.example.weather.interfaces.IWeatherProvider;

/**
 * Command to show current weather at current location.
 */
public class CurrentWeather extends AbstractCommand {
	private final IWeatherProvider weatherProvider;
	private final ILocationProvider locationProvider;

	public CurrentWeather(IWeatherProvider weatherProvider, ILocationProvider locationProvider) {
		super("current");
		this.weatherProvider = weatherProvider;
		this.locationProvider = locationProvider;
	}

	@Override
	public void handleCommand(WeatherApplication context, String[] args) {
		try {
			Weather weather = weatherProvider.forLocation(locationProvider.currentLocation());
			System.out.println("Temperature: " + weather.getTemperature() + "\n" +
					"Forecast:    " + weather.getForecast() + "\n" +
					"Wind:        " + weather.getWind());
		} catch (WeatherLookupException ex) {
			System.err.println("Failed to determine weather for current location");
		} catch (LocationLookupException ex) {
			System.err.println("Failed to determine current location");
		}
	}
}
