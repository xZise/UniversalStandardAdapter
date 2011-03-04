package de.xzise.usa.adapters;

import org.bukkit.plugin.Plugin;

public interface Adapter {

	Plugin getPlugin();
	
	/**
	 * Returns the name of the supported functionality.
	 * @return
	 */
	String getName();
	
}
