package de.xzise.usa.passive;

import de.xzise.usa.adapters.Adapter;

/**
 * This interface marks a class that this class owns adapters which could be
 * registered.
 * 
 * @author Fabian Neundorf
 */
public interface AdapterOwner {

	Adapter[] getAdapters();
	
}
