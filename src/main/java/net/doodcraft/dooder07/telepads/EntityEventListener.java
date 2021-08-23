package net.doodcraft.dooder07.telepads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityEventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        StaticMethods.padScan(event.getBlock(), event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        StaticMethods.padScan(event.getBlock(), event.getPlayer());
    }

    @EventHandler
    public void onEntityUse(EntityInteractEvent event) {
        Block block = event.getBlock();
        StaticMethods.prepareTeleport(event.getEntity(), new Telepad(block));
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (block == null) return;
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            for (String v2 : StaticMethods.validTriggers) {
                if (StaticMethods.getBlockName(block).contains(v2)) {
                    Telepad pad = new Telepad(block, player);
                    TelepadValidateTask validateTask = new TelepadValidateTask(pad, false);
                    validateTask.run();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (TelepadsPlugin.telepadCache.isCached(pad) && validateTask.isValid()) {
                                Telepad telepad = TelepadsPlugin.telepadCache.getTelepad(pad.getId());
                                player.sendMessage(Messages.clickedHeader);
                                player.sendMessage(Messages.id(telepad));
                                player.sendMessage(Messages.time(telepad));
                                player.sendMessage(Messages.owner(telepad));
                                player.sendMessage(Messages.key(telepad));
                            }
                        }
                    }.runTaskLater(TelepadsPlugin.plugin, 1L); // todo make better, kinda slow ~8ms
                    break;
                }
            }
        }
        if (event.getAction().equals(Action.PHYSICAL)) {
            if (block == null) return;
            for (String v2 : StaticMethods.validTriggers) {
                if (StaticMethods.getBlockName(block).contains(v2)) {
                    Telepad pad = new Telepad(block, player);
                    if (TelepadsPlugin.telepadCache.isCached(pad)) {
                        if (pad.getKey().equals(TelepadsPlugin.telepadCache.getTelepad(pad.getId()).getKey())) {
                            StaticMethods.prepareTeleport(event.getPlayer(), pad);
                        } else {
                            TelepadsPlugin.telepadCache.removeFromMemory(TelepadsPlugin.telepadCache.getTelepad(pad.getId()));
                            player.sendMessage(Messages.noLongerValid);
                        }
                    }
                }
            }
        }
    }

    // todo: optional iron elevators? seems related
    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        location.add(0.0D, -1.0D, 0.0D);
        Block block = location.getBlock();
        if (block.getType() == Material.IRON_BLOCK) {
            location.add(0.0D, -3.0D, 0.0D);
            for(int i = location.getBlockY(); i >= -63; --i) {
                location.add(0.0D, -1.0D, 0.0D);
                Block blockUn = location.getBlock();
                if (blockUn.getType() == Material.IRON_BLOCK) {
                    location.add(0.0D, 1.0D, 0.0D);
                    if (location.getBlock().getType() == Material.AIR) {
                        location.add(0.0D, 1.0D, 0.0D);
                        if (location.getBlock().getType() == Material.AIR) {
                            location.add(0.0D, -1.0D, 0.0D);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(TelepadsPlugin.plugin, () -> {
                                player.teleport(location);
                                player.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.5F, 1.0F);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 100, true));
                            }, 5L);
                            break;
                        }
                    }
                }
            }
        }
    }

    // more iron elevator code
    @EventHandler
    public void onJump(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to.getBlockY() != from.getBlockY()) {
            Player player = event.getPlayer();
            Location location = player.getLocation();
            location.add(0.0D, -1.0D, 0.0D);
            Block block = location.getBlock();
            if (block.getType() == Material.IRON_BLOCK) {
                for(int i = location.getBlockY(); i <= 255; ++i) {
                    location = location.add(0.0D, 1.0D, 0.0D);
                    Block blocks = location.getBlock();
                    if (blocks.getType() == Material.IRON_BLOCK) {
                        location.add(0.0D, 1.0D, 0.0D);
                        if (location.getBlock().getType() == Material.AIR) {
                            location.add(0.0D, 1.0D, 0.0D);
                            if (location.getBlock().getType() == Material.AIR) {
                                location.add(0.0D, -1.0D, 0.0D);
                                player.teleport(location);
                                player.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.5F, 1.0F);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 100, true));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}