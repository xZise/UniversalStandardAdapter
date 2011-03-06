package de.xzise.usa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.usa.adapters.Adapter;
import de.xzise.usa.adapters.economy.EconomyAdapter;
import de.xzise.usa.adapters.permission.PermissionAdapter;
import de.xzise.usa.adapters.permission.PermissionDataAdapter;

public class UniveralStandardAdapter extends JavaPlugin {
	
	private static final Map<String, Class<? extends Adapter>> ADAPTER_NAMES = new HashMap<String, Class<? extends Adapter>>();
	
	static {
		// Register all names here
		ADAPTER_NAMES.put("permissions-data", PermissionDataAdapter.class);
		ADAPTER_NAMES.put("permissions", PermissionAdapter.class);
		ADAPTER_NAMES.put("economy", EconomyAdapter.class);
	}
	
	private final Map<Class<? extends Adapter>, Adapter> adapters = new HashMap<Class<? extends Adapter>, Adapter>();
	private final Map<Class<? extends Adapter>, List<AdapterListener>> listeners = new HashMap<Class<? extends Adapter>, List<AdapterListener>>();
	
	@Override
	public void onDisable() {
		Logger.getLogger("Minecraft").info(this.getDescription().getName() + " disabled.");
	}

	@Override
	public void onEnable() {
		Logger.getLogger("Minecraft").info(this.getDescription().getName() + " enabled (Version: " + this.getDescription().getVersion() + ").");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			if (ADAPTER_NAMES.containsKey(args[0])) {
				Adapter adapter = this.getAdapter(args[0]);
				if (adapter == null) {
					sender.sendMessage("Adapter for " + ChatColor.GREEN + args[0] + ChatColor.WHITE + " is " + ChatColor.RED + "not registered.");
				} else {
					sender.sendMessage("Adapter is registered to: " + ChatColor.GREEN + adapter.getPlugin().getDescription().getName() + ChatColor.WHITE + " version " + ChatColor.GREEN + adapter.getPlugin().getDescription().getVersion());
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Incorrect adapter name.");
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Shows a warning if no name for the base class exists.
	 * @param realClass The class of the adapter.
	 * @param baseClass The base class of the adapter.
	 */
	private void showErrorWarning(Class<? extends Adapter> realClass) {
		if (!ADAPTER_NAMES.containsValue(realClass)) {
			Logger.getLogger("Minecraft").warning("[" + this.getDescription().getName() + "] Register an adapter without a name: " + realClass.getName());
		}
	}
	
	/**
	 * Registers an adapter. Returns false if an adapter is already registered.
	 * @param adapter New adapter.
	 * @return If an adapter for this type was already registered.
	 */
	public boolean registerAdapter(Adapter adapter) {		
		this.showErrorWarning(adapter.getClass());
		Adapter a = this.adapters.get(adapter.getClass());
		if (a == null) {
			this.adapters.put(adapter.getClass(), adapter);
			List<AdapterListener> adapterListeners = this.listeners.get(adapter.getClass());
			if (adapterListeners != null) {
				for (AdapterListener adapterListener : adapterListeners) {
					adapterListener.onRegister(adapter);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void unregisterAdapter(Adapter adapter) {
		Adapter old = this.adapters.get(adapter.getClass());
		if (old == adapter) {
			this.adapters.remove(adapter.getClass());
			List<AdapterListener> adapterListeners = this.listeners.get(adapter.getClass());
			if (adapterListeners != null) {
				for (AdapterListener adapterListener : adapterListeners) {
					adapterListener.onUnregister(adapter);
				}
			}
		} else if (old == null) {
			Logger.getLogger("Minecraft").warning("[" + this.getDescription().getName() + "] No adapter is registered.");
		} else {
			Logger.getLogger("Minecraft").warning("[" + this.getDescription().getName() + "] Tried to unregister an adapter which isn't registered.");
		}
	}
	
	public void registerAdapterListener(AdapterListener listener) {
		List<AdapterListener> adapterListeners = this.listeners.get(listener.getAdapterClass());
		if (adapterListeners == null) {
			adapterListeners = new ArrayList<AdapterListener>();
			this.listeners.put(listener.getAdapterClass(), adapterListeners);
		}
		adapterListeners.add(listener);
		Adapter adapter = this.adapters.get(listener.getAdapterClass());
		if (adapter != null) {
			listener.onRegister(adapter);
		}
	}
	
	/**
	 * Gets an adapter for a name.
	 * @param name Adapter name.
	 * @return The adapter.
	 */
	public Adapter getAdapter(String name) {
		Class<? extends Adapter> adapterClass = ADAPTER_NAMES.get(name);
		if (adapterClass != null) {
			return this.adapters.get(adapterClass);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets an adapter for a desired adapter class.
	 * @param adapterClass The class of the desired adapter.
	 * @return The adapter.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Adapter> T getAdapter(Class<T> adapterClass) {
		Adapter a = this.adapters.get(adapterClass);
		if (adapterClass.getClass().isAssignableFrom(a.getClass())) {
			return (T) a;
		} else {
			return null;
		}
	}
}
