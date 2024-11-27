package com.lukeonuke.pvptoggle.service;

import org.bukkit.ChatColor;

public class ChatFormatterService {
    public static String prefix;
    public static String addPrefix(String text){
        return prefix + " " + ChatColor.RESET + text;
    }
    public static String enabled;
    public static String disabled;

    public static String booleanHumanReadable(boolean b){
        return (b ? enabled : disabled) + ChatColor.RESET;
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
