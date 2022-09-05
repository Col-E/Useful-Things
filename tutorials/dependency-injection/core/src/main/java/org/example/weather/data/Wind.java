package org.example.weather.data;

import java.util.Objects;

/**
 * Wind information.
 */
public class Wind {
	private final double speed;
	private final String measurement; // mph / kph
	private final String direction; // N, NE, E, SE, ... etc

	public Wind(double speed, String measurement, String direction) {
		this.speed = speed;
		this.measurement = measurement;
		this.direction = direction;
	}

	public double getSpeed() {
		return speed;
	}

	public String getMeasurement() {
		return measurement;
	}

	public String getDirection() {
		return direction;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Wind wind = (Wind) o;
		if (Double.compare(wind.speed, speed) != 0) return false;
		if (!Objects.equals(direction, wind.direction)) return false;
		return Objects.equals(measurement, wind.measurement);
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(speed);
		result = (int) (temp ^ (temp >>> 32));
		result = 31 * result + (direction != null ? direction.hashCode() : 0);
		result = 31 * result + (measurement != null ? measurement.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("%s %.2f%s", verboseDirection(), speed, measurement);
	}

	private String verboseDirection() {
		switch (direction) {
			case "N":
				return "North";
			case "NE":
				return "North-east";
			case "E":
				return "East";
			case "SE":
				return "South-east";
			case "S":
				return "South";
			case "SW":
				return "South-west";
			case "W":
				return "West";
			case "NW":
				return "North-west";
			default:
				return "?";
		}
	}
}
