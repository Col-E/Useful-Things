package org.example.weather.commands;

import org.example.weather.WeatherApplication;

/**
 * Outline of a runnable task, taking in the executing context and arguments.
 */
public interface ICommand {
	/**
	 * @return Command name.
	 */
	String name();

	/**
	 * @param context
	 * 		Application context to pull values from if necessary.
	 * @param args
	 * 		Arguments.
	 */
	void handleCommand(WeatherApplication context, String[] args);
}
