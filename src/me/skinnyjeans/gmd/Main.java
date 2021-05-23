/**
 * Main handler for the Gameplay-Modulated-difficulty plugin.
 * Here all the default values and commands will be processed and/or initialized.
 *
 * @version 1.1
 * @author SkinnyJeans
 */
package me.skinnyjeans.gmd;

import me.skinnyjeans.gmd.commands.AffinityCommands;
import me.skinnyjeans.gmd.hooks.Metrics;
import me.skinnyjeans.gmd.hooks.PlaceholderAPIExpansion;
import me.skinnyjeans.gmd.tabcompleter.AffinityTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
	
	public DataManager data = new DataManager(this);
	public Affinity af = null;
	
	@Override
	public void onEnable() {
		if (!getConfig().getString("version").equals(Bukkit.getPluginManager().getPlugin("DynamicDifficulty").getDescription().getVersion()) || !getConfig().contains("version",true)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[DynamicDifficulty] Your configuration file is not up to date. Please remove it or update it yourself, because I don't know how to do it with Java without deleting existing configs. Sorry :'(");
		}

		Bukkit.getConsoleSender().sendMessage("[DynamicDifficulty] Thank you for installing DynamicDifficulty!");

		if(data.getConfig().getBoolean("per-player-difficulty")) {
			af = new PlayerAffinity(this);
			Bukkit.getConsoleSender().sendMessage("[DynamicDifficulty] Currently on Per Player Difficulty mode!");
		}
		else {
			af = new WorldAffinity(this);
			Bukkit.getConsoleSender().sendMessage("[DynamicDifficulty] Currently on World Difficulty mode!");
		}
		getServer().getPluginManager().registerEvents(af, this);
		this.getCommand("affinity").setExecutor(new AffinityCommands(af));
		this.getCommand("affinity").setTabCompleter(new AffinityTabCompleter(af));

		Metrics metrics = new Metrics(this, 11417);

		if(data.getConfig().getBoolean("plugin-support.allow-papi"))
			if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
				new PlaceholderAPIExpansion(this, af, data.getConfig().getBoolean("plugin-support.use-prefix")).register();

		saveDataEveryFifteenMinutes();
		onInterval();
	}
	
	@Override
	public void onDisable() {
		af.saveData();
		af = null;
		data = null;
	}
	
	public void onInterval() {
		if(data.getConfig().getInt("on-interval") != 0) {
			int timer = data.getConfig().getInt("interval-timer");
			if(timer > 0) {
				BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		        scheduler.scheduleSyncRepeatingTask(this, () -> {
					if (Bukkit.getOnlinePlayers().size() > 0) {
						af.onInterval();
					}
				}, 0L, 20L*(60L *timer));
			}
		}
	}
	
	public void saveDataEveryFifteenMinutes() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
			if (Bukkit.getOnlinePlayers().size() > 0) {
				af.saveData();
			}
		}, 0L, 20L*(60*15));
	}
}

