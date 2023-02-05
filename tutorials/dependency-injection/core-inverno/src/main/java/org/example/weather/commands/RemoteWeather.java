package org.example.weather.commands;

import org.example.weather.WeatherApplication;
import org.example.weather.data.Location;
import org.example.weather.data.Weather;
import org.example.weather.data.WeatherLookupException;
import org.example.weather.interfaces.IWeatherProvider;

/**
 * Command to show current weather at a remote location.
 */
public class RemoteWeather extends AbstractCommand {
	private final IWeatherProvider weatherProvider;

	public RemoteWeather(IWeatherProvider weatherProvider) {
		super("remote");
		this.weatherProvider = weatherProvider;
	}

	@Override
	public void handleCommand(WeatherApplication context, String[] args) {
		try {
			if (args.length < 2)
				throw new IllegalArgumentException("Missing required arguments: 'latitude' and 'longitude'");
			double latitude = Double.parseDouble(args[0]);
			double longitude = Double.parseDouble(args[1]);
			if (Math.abs(latitude) > 90)
				throw new IllegalArgumentException("Latitude must be within [+90, -90]");
			if (Math.abs(longitude) > 180)
				throw new IllegalArgumentException("Longitude must be within [+180, -180]");
			Weather weather = weatherProvider.forLocation(new Location(latitude, longitude));
			System.out.println("Temperature: " + weather.getTemperature() + "\n" +
					"Forecast:    " + weather.getForecast() + "\n" +
					"Wind:        " + weather.getWind());
		} catch (WeatherLookupException ex) {
			System.err.println("Failed to determine weather for requested location");
		} catch (NumberFormatException ex) {
			System.err.println("Failed to parse lat/lon arguments");
		}
	}
}
