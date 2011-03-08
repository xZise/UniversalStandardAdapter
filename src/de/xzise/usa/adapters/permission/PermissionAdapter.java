package de.xzise.usa.adapters.permission;

import de.xzise.usa.adapters.Adapter;

public interface PermissionAdapter extends Adapter {

	boolean hasPermission(String user, String world, String name);
	
	String[] getGroups(String user, String world);
	
}
