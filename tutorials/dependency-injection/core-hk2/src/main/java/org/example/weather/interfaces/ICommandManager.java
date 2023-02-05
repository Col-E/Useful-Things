package org.example.weather.interfaces;

import org.example.weather.commands.ICommand;
import org.jvnet.hk2.annotations.Contract;

import java.util.Set;

/**
 * Outlines the available commands.
 */
@Contract
public interface ICommandManager {
	/**
	 * @return All recognized commands.
	 */
	Set<ICommand> commands();

	/**
	 * @param name
	 * 		Command name.
	 *
	 * @return Command implementation.
	 */
	ICommand get(String name);
}
