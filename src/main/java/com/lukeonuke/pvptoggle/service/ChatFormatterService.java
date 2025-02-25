package com.lukeonuke.pvptoggle.service;

import org.bukkit.ChatColor;

public class ChatFormatterService {
    public static String addPrefix(String text){
        final ConfigurationService cs = ConfigurationService.getInstance();
        return cs.getPrefix() + " " + ChatColor.RESET + text;
    }


    public static String booleanHumanReadable(boolean b){
        final ConfigurationService cs = ConfigurationService.getInstance();
        return (b ? cs.getDisabled() : cs.getEnabled()) + ChatColor.RESET;
    }

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
