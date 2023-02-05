package org.example.weather.data;

import java.util.Objects;

/**
 * Temperature degrees.
 */
public class Temperature {
	private final double degrees;
	private final String degreesMeasurement; // C or F

	public Temperature(double degrees, String degreesMeasurement) {
		this.degrees = degrees;
		this.degreesMeasurement = degreesMeasurement;
	}

	public double getDegrees() {
		return degrees;
	}

	public String getDegreesMeasurement() {
		return degreesMeasurement;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Temperature temperature = (Temperature) o;
		if (Double.compare(temperature.degrees, degrees) != 0) return false;
		return Objects.equals(degreesMeasurement, temperature.degreesMeasurement);
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(degrees);
		result = (int) (temp ^ (temp >>> 32));
		result = 31 * result + (degreesMeasurement != null ? degreesMeasurement.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("%.2f'%s", degrees, degreesMeasurement);
	}
}
