package de.xzise.usa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.usa.adapters.Adapter;
import de.xzise.usa.adapters.economy.EconomyAdapter;
import de.xzise.usa.adapters.permission.PermissionAdapter;
import de.xzise.usa.adapters.permission.PermissionDataAdapter;
import de.xzise.usa.passive.AdapterOwner;
import de.xzise.usa.passive.AdapterUser;

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
		// Check all plugins
		for (Plugin plugin : this.getServer().getPluginManager().getPlugins()) {
			this.checkPassiveActions(plugin);
		}
		
		ServerListener listener = new ServerListener() {
			@Override
			public void onPluginEnabled(PluginEvent event) {
				UniveralStandardAdapter.this.checkPassiveActions(event.getPlugin());
		    }

			@Override
		    public void onPluginDisabled(PluginEvent event) {
				UniveralStandardAdapter.this.checkPassiveActions(event.getPlugin());				
		    }
		};
		
		this.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, listener, Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, listener, Priority.Normal, this);
		
		Logger.getLogger("Minecraft").info(this.getDescription().getName() + " enabled (Version: " + this.getDescription().getVersion() + ").");
	}
	
	private class AdapterUserListener implements AdapterListener {

		private final Class<? extends Adapter> adapterClass;
		private final AdapterUser user;
		
		public AdapterUserListener(Class<? extends Adapter> adapterClass, AdapterUser user) {
			this.adapterClass = adapterClass;
			this.user = user;
		}
		
		public AdapterUser getUser() {
			return this.user;
		}
		
		@Override
		public Class<? extends Adapter> getAdapterClass() {
			return this.adapterClass;
		}

		@Override
		public void onRegister(Adapter adapter) {
			this.user.adapterRegistered(adapter);
		}

		@Override
		public void onUnregister(Adapter adapter) {
			this.user.adapterUnregistered(adapter);
		}		
	}
	
	private void checkPassiveActions(Plugin plugin) {
		if (plugin.isEnabled()) {
			if (plugin instanceof AdapterOwner) {
				// Register all adapters this plugin owns.
				for (Adapter adapter : ((AdapterOwner) plugin).getAdapters()) {
					this.registerAdapter(adapter);
				}
			}
			
			if (plugin instanceof AdapterUser) {
				for (Class<? extends Adapter> clazz : ((AdapterUser) plugin).getAdapters()) {
					this.registerAdapterListener(new AdapterUserListener(clazz, (AdapterUser) plugin));
				}
			}
		} else {
			if (plugin instanceof AdapterOwner) {
				// Unregister all adapters of this plugin.
				//TODO: Maybe make this smoother?
				for (Adapter adapter : this.adapters.values()) {
					if (adapter.getPlugin() == plugin) {
						this.unregisterAdapter(adapter);
					}
				}	
			}
			
			if (plugin instanceof AdapterUser) {
				for (Class<? extends Adapter> clazz : ((AdapterUser) plugin).getAdapters()) {
					unregisterAdapterListener(plugin, clazz);
				}
			}
		}
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
	
	@SuppressWarnings("unchecked")
	private List<Class<? extends Adapter>> getAdapterInterfaces(Adapter adapter) {
		List<Class<? extends Adapter>> result = new LinkedList<Class<? extends Adapter>>();
		Class<?>[] interfaces = adapter.getClass().getInterfaces();
		for (Class<?> clazz : interfaces) {
			if (Adapter.class.isInstance(clazz)) {
				result.add((Class<? extends Adapter>) clazz);
			}
		}
		return result;
	}
	
	/**
	 * Registers an adapter. Returns false if an adapter is already registered.
	 * @param adapter New adapter.
	 * @return If an adapter for this type was already registered.
	 */
	public boolean registerAdapter(Adapter adapter) {		
		boolean result = true;
		List<Class<? extends Adapter>> interfaces = this.getAdapterInterfaces(adapter);
		for (Class<? extends Adapter> clazz : interfaces) {
			result = result && this.registerAdapter(adapter, (Class<? extends Adapter>) clazz);
		}
		return result;
	}
	
	private boolean registerAdapter(Adapter adapter, Class<? extends Adapter> adapterClass) {
		this.showErrorWarning(adapterClass);
		Adapter a = this.adapters.get(adapterClass);
		if (a == null) {
			this.adapters.put(adapterClass, adapter);
			List<AdapterListener> adapterListeners = this.listeners.get(adapterClass);
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
		List<Class<? extends Adapter>> interfaces = this.getAdapterInterfaces(adapter);
		for (Class<? extends Adapter> clazz : interfaces) {
			this.unregisterAdapter(adapter, clazz);
		}
	}
	
	private void unregisterAdapter(Adapter adapter, Class<? extends Adapter> adapterClass) {
		Adapter old = this.adapters.get(adapterClass);
		if (old == adapter) {
			this.adapters.remove(adapterClass);
			List<AdapterListener> adapterListeners = this.listeners.get(adapterClass);
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
	
	public void unregisterAdapterListener(AdapterListener listener) {
		List<AdapterListener> adapterListeners = this.listeners.get(listener.getAdapterClass());
		if (adapterListeners != null) {
			adapterListeners.remove(listener);
		}
	}

	public void unregisterAdapterListener(Plugin plugin, Class<? extends Adapter> clazz) {
		List<AdapterListener> adapterListeners = this.listeners.get(clazz);
		if (adapterListeners != null) {
			List<AdapterListener> pluginListeners = new LinkedList<AdapterListener>();
			for (AdapterListener adapterListener : adapterListeners) {
				if (adapterListener instanceof AdapterUserListener && ((AdapterUserListener) adapterListener).getUser() == plugin) {
					pluginListeners.add(adapterListener);
				}
			}
			adapterListeners.removeAll(pluginListeners);
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
		if (adapterClass.isInstance(a)) {
			return (T) a;
		} else {
			return null;
		}
	}
}
