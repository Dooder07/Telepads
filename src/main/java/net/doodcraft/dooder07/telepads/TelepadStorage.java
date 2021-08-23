package net.doodcraft.dooder07.telepads;

import java.io.File;

public class TelepadStorage extends YamlFile {

    YamlFile padFile;

    public TelepadStorage() {
        super(TelepadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "telepads.yml");
        this.padFile = this;
        this.save();
    }

    public void loadTelepads() {
        for (String id : this.getKeys(false)) {
            loadTelepad(id);
        }
    }

    public void dumpTelepads() {
        for (Telepad pad : TelepadsPlugin.telepadCache.getTelepads().values()) {
            saveTelepad(pad);
        }
    }

    public void loadTelepad(String id) {
        String key = "";
        String time = "";
        String owner = "";
        if (this.contains(id + ".Key")) {
            key = this.getString(id + ".Key");
        }
        if (this.contains(id + ".Time")) {
            time = this.getString(id + ".Time");
        }
        if (this.contains(id + ".Owner")) {
            owner = this.getString(id + ".Owner");
        }
        Telepad pad = new Telepad(id, key, time, owner);
        TelepadsPlugin.telepadCache.fastCache(pad);
    }

    public void saveTelepad(Telepad telepad) {
        String id = String.valueOf(telepad.getId());
        if (this.getKeys(false).contains(id)) {
            this.remove(id);
        }
        this.add(id + ".Key", telepad.getKey());
        this.add(id + ".Time", telepad.getCreateTime());
        this.add(id + ".Owner", telepad.getCreatorAsString());
    }

    public void deleteTelepad(Telepad telepad) {
        this.remove(telepad.getId());
        this.save();
    }
}
