package net.doodcraft.dooder07.telepads;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;

public class TelepadIdentifyTask extends BukkitRunnable {

    private Block block;
    private String id;
    private String key;

    public TelepadIdentifyTask(Block block) {
        this.block = block;
        run();
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public void run() {
        for (String trigger : StaticMethods.validTriggers) {
            if (StaticMethods.getBlockName(block).contains(trigger)) {
                this.block = block.getRelative(BlockFace.DOWN);
            }
        }
        String blockId = StaticMethods.getBlockName(block);
        for (String v : StaticMethods.validCenters) {
            if (blockId.contains(v)) {
                Block u = block.getRelative(BlockFace.UP);
                for (String v2 : StaticMethods.validTriggers) {
                    if (StaticMethods.getBlockName(u).contains(v2)) {
                        ArrayList<String> blockNames = new ArrayList<>();
                        for (BlockFace face : StaticMethods.faces) {
                            Block relative = block.getRelative(face);
                            if (relative.getType().isOccluding()) {
                                String name = StaticMethods.getBlockName(relative);
                                for (String filtered : StaticMethods.filter) {
                                    if (name.contains(filtered)) {
                                        this.id = "invalid";
                                        this.key = "invalid";
                                        return;
                                    }
                                }
                                blockNames.add(name);
                            } else {
                                this.id = "invalid";
                                this.key = "invalid";
                                return;
                            }
                        }
                        Collections.sort(blockNames);
                        blockNames.add(0, StaticMethods.getBlockName(block));
                        this.id = StaticMethods.locationToString(block.getLocation());
                        this.key = String.join("|", blockNames);
                        break;
                    }
                    this.id = "invalid";
                    this.key = "invalid";
                    return;
                }
                return;
            }
        }
    }
}