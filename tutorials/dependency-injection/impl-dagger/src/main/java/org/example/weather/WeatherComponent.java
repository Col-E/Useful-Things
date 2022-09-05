package org.example.weather;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {WeatherModule.class})
public interface WeatherComponent {
	WeatherApplication createApplication();
}
