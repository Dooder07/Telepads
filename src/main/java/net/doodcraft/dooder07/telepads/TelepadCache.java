package net.doodcraft.dooder07.telepads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class TelepadCache {

    private HashMap<String, Telepad> telepads; // id, telepad object
    private HashMap<String, ArrayList<String>> links; // key, padids

    public TelepadCache() {
        this.telepads = new HashMap<>();
        this.links = new HashMap<>();
    }

    public Telepad getTelepad(String id) {
        if (this.telepads.containsKey(id)) {
            return telepads.get(id);
        } else {
            return null;
        }
    }

    public HashMap<String, Telepad> getTelepads() {
        return this.telepads;
    }

    public void fastCache(Telepad telepad) {
        String id = telepad.getId();
        String key = telepad.getKey();
        String block = StaticMethods.getBlockName(telepad.getBlock());
        TelepadNetwork network;
        if (!TelepadsPlugin.telepadNetworks.networkExists(block)) {
            network = new TelepadNetwork(block);
            TelepadsPlugin.telepadNetworks.addNetwork(network);
            Bukkit.getLogger().log(Level.INFO, "New Telepad Network Created: " + network.getNetworkId());
        } else {
            network = TelepadsPlugin.telepadNetworks.getNetwork(block);
        }
        this.telepads.put(id, telepad);
        if (links.containsKey(key)) {
            ArrayList<String> linked = links.get(key);
            if (!linked.contains(id)) {
                linked.add(id);
                links.put(key, linked);
            }
        } else {
            links.put(key, new ArrayList<>(Arrays.asList(id)));
        }
    }

    public Boolean addToCache(Telepad telepad) {
        if (isCached(telepad)) return false; // optimize later async
        String id = telepad.getId();
        String key = telepad.getKey();
        String block = StaticMethods.getBlockName(telepad.getBlock());

        // network implementation start:
        TelepadNetwork network;
        if (!TelepadsPlugin.telepadNetworks.networkExists(block)) {
            network = new TelepadNetwork(block);
            TelepadsPlugin.telepadNetworks.addNetwork(network);
        } else {
            network = TelepadsPlugin.telepadNetworks.getNetwork(block);
        }

        // add pad
        this.telepads.put(id, telepad); // optimize later async

        // links
        if (links.containsKey(key)) {
            ArrayList<String> linked = links.get(key);
            if (!linked.contains(id)) {
                linked.add(id);
                links.put(key, linked);
            }
        } else {
            links.put(key, new ArrayList<>(Arrays.asList(id)));
        }

        // sync stuff optimize later
        new BukkitRunnable() {
            @Override
            public void run() {
                if (network.lightningEnabled()) {
                    telepad.getBlock().getWorld().strikeLightningEffect(telepad.getBlock().getLocation());
                }
                Player player = telepad.getCreatorPlayer();
                player.sendMessage(Messages.createdHeader);
                player.sendMessage(Messages.id(telepad));
                player.sendMessage(Messages.key(telepad));
                TelepadsPlugin.padStorage.saveTelepad(telepad);
                TelepadsPlugin.padStorage.save();
            }
        }.runTaskLater(TelepadsPlugin.plugin, 1L); // todo cache or async file writes? ~10ms
        return true;
    }

    public Boolean removeFromMemory(Telepad telepad) {
        if (!(isCached(telepad))) return false; // optimize later async
        String id = telepad.getId();
        String key = telepad.getKey();
        String block = StaticMethods.getBlockName(telepad.getBlock());

        // network implementation start:
        TelepadNetwork network;
        if (!TelepadsPlugin.telepadNetworks.networkExists(block)) {
            network = new TelepadNetwork(block);
            TelepadsPlugin.telepadNetworks.addNetwork(network);
        } else {
            network = TelepadsPlugin.telepadNetworks.getNetwork(block);
        }

        // links
        if (links.containsKey(key)) {
            ArrayList<String> l = links.get(key);
            l.remove(id);
            if (l.size() > 0) {
                links.put(key, l);
            } else {
                links.remove(key);
            }
        }

        // remove pad
        telepads.remove(id);

        // sync stuff optimize later
        new BukkitRunnable() {
            @Override
            public void run() {
                if (network.lightningEnabled()) {
                    telepad.getBlock().getWorld().strikeLightningEffect(telepad.getBlock().getLocation());
                }
                if (telepad.getCreatorPlayer() != null) {
                    Player player = telepad.getCreatorPlayer();
                    player.sendMessage(Messages.destroyedHeader);
                    player.sendMessage(Messages.id(telepad));
                    player.sendMessage(Messages.key(telepad));
                }
                TelepadsPlugin.padStorage.deleteTelepad(telepad);
                TelepadsPlugin.padStorage.save();
            }
        }.runTaskLater(TelepadsPlugin.plugin, 1L);
        return true;
    }

    public Boolean isCached(Telepad telepad) {
        if (telepad.getId() == null || telepad.getKey() == null) {
            return false;
        }
        return telepads.containsKey(telepad.getId());
    }

    public ArrayList<String> getLinks(Telepad telepad) {
        return links.get(telepad.getKey());
    }
}