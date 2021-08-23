package net.doodcraft.dooder07.telepads;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityTeleportTask extends BukkitRunnable {

    private Entity e;
    private Player p;
    private Location l;

    public EntityTeleportTask(Entity entity, Location location) {
        this.e = entity;
        this.l = location;
        if (e instanceof Player) {
            p = (Player) e;
            this.l.setYaw(p.getLocation().getYaw());
            this.l.setPitch(p.getLocation().getPitch());
        }
    }

    @Override
    public void run() {
        PaperLib.teleportAsync(e, l.add(0.5, 1, 0.5)).thenAccept(result -> {
            if (result) {
                StaticMethods.coolDowns.put(e.getUniqueId(), System.currentTimeMillis());
            } else {
                e.sendMessage(StaticMethods.addColor("&cSomething went wrong ."));
            }
        });
    }
}
