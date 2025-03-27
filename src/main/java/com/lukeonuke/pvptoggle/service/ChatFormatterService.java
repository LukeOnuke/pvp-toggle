package com.lukeonuke.pvptoggle.service;

import org.bukkit.ChatColor;

/**
 * Utility class used for formatting text.
 * */
public class ChatFormatterService {
    /**
     * Add the configurable prefix to text.
     * @param text The text to which the prefix will be added.
     * */
    public static String addPrefix(String text){
        final ConfigurationService cs = ConfigurationService.getInstance();
        return cs.getPrefix() + " " + ChatColor.RESET + text;
    }

    /**
     * Interprets a boolean as a configurable human friendly string.<br>
     * By default, it's true = "§aProtected" and false = "§cVulnerable". See config.yml fields
     * <b>enabled</b> and <b>disabled</b>.
     * @param b The boolean to be interpreted.
     * @return Human friendly interpretation of boolean. (true = "§aProtected" and false = "§cVulnerable")
     * */
    public static String booleanHumanReadable(boolean b){
        final ConfigurationService cs = ConfigurationService.getInstance();
        return (b ? cs.getDisabled() : cs.getEnabled()) + ChatColor.RESET;
    }

    /**
     * Formats time into readable format.
     * @param ms Milliseconds of time.
     * @return Readable time.
     */
    public static String formatTime(long ms) {
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " and " + seconds + " second" + (seconds != 1 ? "s" : "");
        } else {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        }
    }
}
