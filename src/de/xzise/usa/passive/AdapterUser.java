package de.xzise.usa.passive;

import de.xzise.usa.adapters.Adapter;

public interface AdapterUser {

	Class<? extends Adapter>[] getAdapters();
	
	void adapterRegistered(Adapter adapter);
	
	void adapterUnregistered(Adapter adapter);
	
}
