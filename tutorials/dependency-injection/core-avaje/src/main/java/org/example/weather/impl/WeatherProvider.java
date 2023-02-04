package org.example.weather.impl;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import org.example.weather.data.*;
import org.example.weather.interfaces.IWeatherProvider;
import org.example.weather.util.HTTP;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation to lookup weather based on location.
 */
@Singleton
public class WeatherProvider implements IWeatherProvider {
	private static final String API_POINTS_URL = "https://api.weather.gov/points/";
	private final Map<String, String> pointsJsonCache = new HashMap<>(); // cache location per roughly 10km via coords 'XX.X'

	@Override
	public Weather forLocation(@Nonnull Location location) throws WeatherLookupException {
		try {
			// Lookup weather point from location
			String pointsJson = getPointsJson(location);
			JsonObject pointsRoot = Json.parse(pointsJson).asObject();
			JsonObject pointsProperties = pointsRoot.get("properties").asObject();
			String forecastHourlyUrl = pointsProperties.getString("forecastHourly", null);
			if (forecastHourlyUrl == null) {
				throw new IOException("Weather lookup API did not yielded 'forecastHourly' property");
			}
			// Lookup weather from point
			String forecastContent = requestForecastJson(forecastHourlyUrl);
			JsonObject forecastRoot = Json.parse(forecastContent).asObject();
			JsonObject forecastProperties = forecastRoot.get("properties").asObject();
			JsonArray forecastPeriods = forecastProperties.get("periods").asArray();
			JsonObject forecastCurrentPeriod = forecastPeriods.get(0).asObject();
			// Parsing the current period
			String forecast = forecastCurrentPeriod.get("shortForecast").asString();
			Temperature temperature = new Temperature(
					forecastCurrentPeriod.get("temperature").asInt(),
					forecastCurrentPeriod.get("temperatureUnit").asString());
			String[] windSpeed = forecastCurrentPeriod.get("windSpeed").asString().split("\\s+");
			String windDirection = forecastCurrentPeriod.get("windDirection").asString();
			Wind wind = new Wind(Double.parseDouble(windSpeed[0]), windSpeed[1], windDirection);
			return new Weather(temperature, wind, forecast);
		} catch (IOException ex) {
			throw new WeatherLookupException(ex);
		}
	}

	private String getPointsJson(Location location) throws IOException {
		String key = location.getFormattedLatitude(1) + "," + location.getFormattedLongitude(1);
		String cachedJson = pointsJsonCache.get(key);
		if (cachedJson != null)
			return cachedJson;
		String liveJson = requestPointsJson(location);
		pointsJsonCache.put(key, liveJson);
		return liveJson;
	}

	private String requestForecastJson(String forecastHourlyUrl) throws IOException {
		return HTTP.readString(forecastHourlyUrl,
				status -> "Weather forecast API '" + forecastHourlyUrl + "' yielded status code: " + status);
	}

	private String requestPointsJson(Location location) throws IOException {
		return HTTP.readString(API_POINTS_URL + location.getLatitude() + "," + location.getLongitude(),
				status -> "Weather points API '" + API_POINTS_URL + "' yielded status code: " + status);
	}
}
