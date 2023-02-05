package org.example.weather.interfaces;

import org.example.weather.commands.ICommand;
import org.jvnet.hk2.annotations.Contract;

import java.util.Set;

/**
 * Responsible for substantiating command implementations.
 */
@Contract
public interface ICommandLoader {
	Set<ICommand> load();
}
