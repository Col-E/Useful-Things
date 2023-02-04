package org.example.weather;

public class Main {
	public static void main(String[] args) {
		// Use the generated component implementation to create a component instance.
		WeatherComponent component = WeatherComponent_Impl.factory().create();
		// Use the instance to create an application and run it.
		component.application().run();
	}
}
