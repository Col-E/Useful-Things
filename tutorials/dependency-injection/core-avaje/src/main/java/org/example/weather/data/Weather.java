package org.example.weather.data;

import java.util.Objects;

/**
 * Weather summary for temperature, wind, and rain levels.
 */
public class Weather {
	private final Temperature temperature;
	private final Wind wind;
	private final String forecast;

	public Weather(Temperature temperature, Wind wind, String forecast) {
		this.temperature = temperature;
		this.wind = wind;
		this.forecast = forecast;
	}

	public Temperature getTemperature() {
		return temperature;
	}

	public Wind getWind() {
		return wind;
	}

	public String getForecast() {
		return forecast;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Weather weather = (Weather) o;
		if (!Objects.equals(temperature, weather.temperature)) return false;
		if (!Objects.equals(wind, weather.wind)) return false;
		return Objects.equals(forecast, weather.forecast);
	}

	@Override
	public int hashCode() {
		int result = temperature != null ? temperature.hashCode() : 0;
		result = 31 * result + (wind != null ? wind.hashCode() : 0);
		result = 31 * result + (forecast != null ? forecast.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Weather{" +
				"temperature=" + temperature +
				", wind=" + wind +
				", forecast=" + forecast +
				'}';
	}
}
