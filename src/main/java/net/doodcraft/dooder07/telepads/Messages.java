package net.doodcraft.dooder07.telepads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Messages {

    public static String noDestination = StaticMethods.addColor("&cThis Telepad has no valid destination.");
    public static String buildAnother = StaticMethods.addColor("&7Create or activate another with the same key blocks.");
    public static String createdHeader = StaticMethods.addColor("&2&m====&2[ &aTelepad Created &2]&m====");
    public static String destroyedHeader = StaticMethods.addColor("&4&m====&4[ &cTelepad Destroyed &4]&m====");
    public static String clickedHeader = StaticMethods.addColor("&5&m====&5[ &dTelepad Information &5]&m====");
    public static String noLongerValid = StaticMethods.addColor("&cThe destination Telepad was broken or invalid.");

    public static String id(Telepad telepad) {
        return StaticMethods.addColor(" &7Location: &f" + telepad.getId().replaceAll(",", ", "));
    }

    public static String key(Telepad telepad) {
        StringBuilder result = new StringBuilder(" &7Key Blocks: &f\n");
        int i = 0;
        for (String s : telepad.getKey().split("\\|")) {
            if (i == 0) result.append("   &7Center: &6");
            result.append(" [").append(s.replaceAll("minecraft:", "")).append("]\n");
            if (i < 4) result.append("   &7Side: &e");
            i++;
        }
        return StaticMethods.addColor(result.toString());
    }

    public static String time(Telepad telepad) {
        return StaticMethods.addColor(" &7Time: &f" + convertTime(telepad.getCreateTime()));
    }

    public static String owner(Telepad telepad) {
        if (Bukkit.getPlayer(telepad.getCreatorAsUUID()) != null) {
            Player player = Bukkit.getPlayer(telepad.getCreatorAsUUID());
            if (player.isOnline()) {
                return StaticMethods.addColor(" &7Owner: &f" + player.getName());
            }
        }
        return StaticMethods.addColor(" &7Owner: &f" + telepad.getCreatorAsString());
    }

    private static String convertTime(long time) {
        Date d = new Date(time);
        Format f = new SimpleDateFormat("MM/dd/yyyy | HH:mm:ss");
        return f.format(d);
    }
}
