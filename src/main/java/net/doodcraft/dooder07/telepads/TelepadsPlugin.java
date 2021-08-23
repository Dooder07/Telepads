package net.doodcraft.dooder07.telepads;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TelepadsPlugin extends JavaPlugin {

    public static Plugin plugin;
    public static TelepadCache telepadCache;
    public static TelepadNetworks telepadNetworks;
    public static TelepadStorage padStorage;

    @Override
    public void onEnable() {
        plugin = this;
        registerListeners();
        telepadNetworks = new TelepadNetworks();
        telepadCache = new TelepadCache();
        padStorage = new TelepadStorage();
        padStorage.loadTelepads();
        getLogger().info("Telepads is now enabled!");
    }

    @Override
    public void onDisable() {
        padStorage.dumpTelepads();
        padStorage.save();
        getLogger().info("Telepads is now disabled!");
    }

    public void registerListeners() {
        registerEvents(this, new EntityEventListener());
    }

    public static void registerEvents(Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
}