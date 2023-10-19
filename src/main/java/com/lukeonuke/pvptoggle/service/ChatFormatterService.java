package com.lukeonuke.pvptoggle.service;

import org.bukkit.ChatColor;

public class ChatFormatterService {
    public static String addPrefix(String text){
        return ChatColor.DARK_RED + "[PVP TOGGLE] " + ChatColor.RESET + text;
    }

    public static String booleanHumanReadable(boolean b){
        return (b ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF") + ChatColor.RESET;
    }
}
