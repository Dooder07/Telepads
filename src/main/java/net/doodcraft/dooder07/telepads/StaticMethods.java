package net.doodcraft.dooder07.telepads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class StaticMethods {

    public static Random random = new Random();
    public static BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    // todo make a config once the core is fully functional
    public static ArrayList<String> validTriggers = new ArrayList<String>(Arrays.asList("pressure_plate"));
    public static ArrayList<String> validCenters = new ArrayList<String>(Arrays.asList("diamond_block", "lodestone"));
    public static ArrayList<String> filter = new ArrayList<String>(Arrays.asList("air", "dirt", "path", "grass", "gravel", "sand"));

    public static String addColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getBlockName(Block block) {
        return block.getBlockData().getAsString().split("\\[")[0];
    }

    public static String locationToString(Location location) {
        if (location.getWorld() != null) {
            return "w:" + location.getWorld().getName() + " x:" + (int) location.getX() + " y:" + (int) location.getY() + " z:" + (int) location.getZ();
        } else {
            return "invalid";
        }
    }

    public static Location stringToLocation(String locString) {
        String[] split = locString.split(":");
        String world = split[1].replaceAll(" x", "");
        double x = Double.parseDouble(split[2].replaceAll(" y", "")); // todo make this better;
        double y = Double.parseDouble(split[3].replaceAll(" z", ""));
        double z = Double.parseDouble(split[4]);
        Bukkit.getLogger().log(Level.INFO, world + "|" + x + "|" + y + "|" + z);
        if (Bukkit.getWorld(world) != null) {
            return new Location(Bukkit.getWorld(world), x, y, z);
        }
        return null;
    }

    public static void padScan(Block block, Player player) {
        Telepad c = new Telepad(block, player);
        Telepad n = new Telepad(block.getRelative(BlockFace.NORTH), player);
        Telepad e = new Telepad(block.getRelative(BlockFace.EAST), player);
        Telepad s = new Telepad(block.getRelative(BlockFace.SOUTH), player);
        Telepad w = new Telepad(block.getRelative(BlockFace.WEST), player);
        for (Telepad pad : new ArrayList<>(Arrays.asList(c, n, e, s, w))) {
            TelepadValidateTask validateTask = new TelepadValidateTask(pad, true);
            validateTask.runTaskLater(TelepadsPlugin.plugin, 1L);
        }
    }

    public static ConcurrentHashMap<UUID, Long> coolDowns = new ConcurrentHashMap<>();

    public static void prepareTeleport(Entity e, Telepad p) {
        if (TelepadsPlugin.telepadCache.getLinks(p) == null) return;
        ArrayList<String> links = new ArrayList<String>(TelepadsPlugin.telepadCache.getLinks(p));
        links.remove(p.getId());
        teleportEntity(e, p, links);
    }

    public static void teleportEntity(Entity entity, Telepad from, ArrayList<String> links) {
        String netId = StaticMethods.getBlockName(from.getBlock().getRelative(BlockFace.DOWN)); // todo make this better
        Bukkit.getLogger().log(Level.INFO, "Teleporting entity on network: " + netId);
        Bukkit.getLogger().log(Level.INFO, "Telepad Block: " + StaticMethods.getBlockName(from.getBlock()));
        if (TelepadsPlugin.telepadNetworks.networkExists(netId)) {
            if (!(TelepadsPlugin.telepadNetworks.getNetwork(netId)).isEnabled()) return;
        }
        Long currentTime = System.currentTimeMillis();
        UUID uuid = entity.getUniqueId();
        if (coolDowns.containsKey(uuid)) {
            if (currentTime - coolDowns.get(uuid) <= 1300) {
                coolDowns.put(uuid, currentTime);
                return;
            } else {
                coolDowns.remove(uuid);
            }
        }
        if (links.size() <= 0) {
            entity.sendMessage(Messages.noDestination);
            entity.sendMessage(Messages.buildAnother);
            entity.sendMessage(Messages.key(from));
            coolDowns.put(uuid, currentTime);
            return;
        }
        int target = random.nextInt(links.size());
        String targetId = links.get(target);
        Telepad targetPad = TelepadsPlugin.telepadCache.getTelepad(targetId);
        TelepadValidateTask validateTask = new TelepadValidateTask(targetPad, StaticConfig.destroyInvalidOnTP);
        validateTask.run();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (validateTask.isValid() && !targetPad.getId().equals(from.getId())) {
                    Location loc = targetPad.getBlock().getLocation();
                    new EntityTeleportTask(entity, loc).run();
                } else {
                    links.remove(targetPad.getId());
                    teleportEntity(entity, from, links);
                }
            }
        }.runTaskLater(TelepadsPlugin.plugin, 1L);
    }
}
