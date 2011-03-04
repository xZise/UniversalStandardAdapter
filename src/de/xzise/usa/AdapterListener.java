package de.xzise.usa;

import de.xzise.usa.adapters.Adapter;

public interface AdapterListener {
	
	Class<? extends Adapter> getAdapterClass();
	
	void onRegister(Adapter adapter);

	void onUnregister(Adapter adapter);
}
