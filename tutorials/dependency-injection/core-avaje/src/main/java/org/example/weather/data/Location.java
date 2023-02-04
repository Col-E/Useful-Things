package org.example.weather.data;

/**
 * Latitude/longitude location.
 */
public class Location {
	private final double latitude;
	private final double longitude;

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getFormattedLatitude(int dpAccuracy) {
		return String.format("%." + dpAccuracy + "f", latitude);
	}

	public String getFormattedLongitude(int dpAccuracy) {
		return String.format("%." + dpAccuracy + "f", longitude);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Location location = (Location) o;
		if (Double.compare(location.latitude, latitude) != 0) return false;
		return Double.compare(location.longitude, longitude) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return latitude + ", " + longitude;
	}
}
