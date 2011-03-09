package de.xzise.usa.adapters.help;

/**
 * Minimal help adapter. The plugins can register help texts here. This adapter
 * based on the Help plugin from tkelly.
 * 
 * @author Fabian Neundorf
 */
public interface HelpAdapter {

	/**
	 * Registers the help description of the command. This help will be always
	 * visible.
	 * 
	 * @param command Help data for an command.
	 * @return If the help could be registered.
	 */
	boolean registerCommand(CommandHelp command);
}
