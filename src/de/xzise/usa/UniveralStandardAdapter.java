package de.xzise.usa;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.usa.adapters.Adapter;
import de.xzise.usa.adapters.EconomyAdapter;
import de.xzise.usa.adapters.PermissionAdapter;

public class UniveralStandardAdapter extends JavaPlugin {
	
	private static final Map<String, Class<? extends Adapter>> ADAPTER_NAMES = new HashMap<String, Class<? extends Adapter>>();
	
	static {
		ADAPTER_NAMES.put("permissions", PermissionAdapter.class);
		ADAPTER_NAMES.put("economy", EconomyAdapter.class);
	}
	
	private Map<Class<? extends Adapter>, Adapter> adapters = new HashMap<Class<? extends Adapter>, Adapter>();
	
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
	
	public boolean registerAdapter(Adapter adapter) {
		Adapter a = this.adapters.get(adapter.getClass());
		if (a == null) {
			this.adapters.put(adapter.getClass(), adapter);
			return true;
		} else {
			return false;
		}
	}
	
	public Adapter getAdapter(String name) {
		Class<? extends Adapter> adapterClass = ADAPTER_NAMES.get(name);
		if (adapterClass != null) {
			return this.adapters.get(adapterClass);
		} else {
			return null;
		}
	}
	
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
