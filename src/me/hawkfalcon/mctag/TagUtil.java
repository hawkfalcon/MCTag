package me.hawkfalcon.mctag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TagUtil {

/**
* Broadcasts a message.
*
* @param message The message.
*/
    public void broadcast(String message) {
        if (message.isEmpty()) {
            return;
        }
        Bukkit.getServer().broadcastMessage(parseColors(message));
    }

/**
* Send ChatColor formatted message to player.
*
* @param player The player.
* @param message The message.
*/
    public void message(Player player, String message) {
        if (message.isEmpty()) {
            return;
        }

        player.sendMessage(parseColors(message));
    }

    /**
* Parses colors in string.
*
* @param msg The string.
* @return The formatted string.
*/
    public String parseColors(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}