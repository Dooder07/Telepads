package net.doodcraft.dooder07.telepads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class Telepad {

    private String id;
    private String key;
    private Long createTime;
    private String creator;
    private Block block;

    public Telepad(String id, String key, String time, String owner) {
        this.id = id;
        this.key = key;
        this.createTime = Long.valueOf(time);
        this.creator = owner;
    }

    public Telepad(Block block, Player player) {
        this.block = block;
        TelepadIdentifyTask task = new TelepadIdentifyTask(block);
        this.setId(task.getId());
        this.setKey(task.getKey());
        this.createTime = System.currentTimeMillis();
        this.creator = player.getUniqueId().toString();
    }

    public Telepad(Block block) {
        this.block = block;
        TelepadIdentifyTask task = new TelepadIdentifyTask(block);
        this.setId(task.getId());
        this.setKey(task.getKey());
        this.createTime = System.currentTimeMillis();
    }

    private void setId(String id) {
        this.id = id;
    }

    private void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public Block getBlock() {
        if (this.block == null) {
            Bukkit.getLogger().log(Level.INFO, "Getting a block from a location from a string for " + this.id);
            Bukkit.getLogger().log(Level.INFO, StaticMethods.stringToLocation(this.id).toString());
            Location loc = StaticMethods.stringToLocation(this.id);
            if (loc != null) {
                this.block = loc.getBlock();
            }
        }
        return this.block;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public String getCreatorAsString() {
        return this.creator;
    }

    public UUID getCreatorAsUUID() {
        return UUID.fromString(this.creator);
    }

    public OfflinePlayer getCreatorOfflinePlayer() {
        return Bukkit.getOfflinePlayer(getCreatorAsUUID());
    }

    public Player getCreatorPlayer() {
        if (Bukkit.getPlayer(getCreatorAsUUID()) != null) {
            return Bukkit.getPlayer(getCreatorAsUUID());
        }
        return null;
    }

    public Boolean validate() {
        TelepadValidateTask validateTask = new TelepadValidateTask(this, false);
        validateTask.runTaskLater(TelepadsPlugin.plugin, 1L);
        return validateTask.isValid();
    }

    public ArrayList<String> getLinks() {
        return new ArrayList<>(TelepadsPlugin.telepadCache.getLinks(this));
    }

    public TelepadNetwork getNetwork() {
        Bukkit.getLogger().log(Level.INFO, "Getting network name: " + StaticMethods.getBlockName(getBlock()));
        return TelepadsPlugin.telepadNetworks.getNetwork(StaticMethods.getBlockName(getBlock()));
    }
}
