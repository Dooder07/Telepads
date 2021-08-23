package net.doodcraft.dooder07.telepads;

import org.bukkit.scheduler.BukkitRunnable;

public class TelepadValidateTask extends BukkitRunnable {

    private final Telepad telepad;
    private Boolean valid;
    private Boolean destroy;

    public TelepadValidateTask(Telepad telepad, Boolean destroy) {
        this.telepad = telepad;
        this.valid = false;
        this.destroy = destroy;
    }

    public Boolean isValid(){
        return this.valid;
    }

    @Override
    public void run() {
        TelepadIdentifyTask identity = new TelepadIdentifyTask(telepad.getBlock());
        if (identity.getId() != null && identity.getKey() != null) {
            if (identity.getId().equals("invalid") || identity.getKey().equals("invalid")) {
                this.valid = false;
            } else {
                TelepadsPlugin.telepadCache.addToCache(telepad);
                this.valid = true;
                return;
            }
        }
        if (this.destroy) TelepadsPlugin.telepadCache.removeFromMemory(telepad);
    }
}
