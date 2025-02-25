package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.ConfigurationService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PVPStatusCommand implements CommandExecutor{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final ConfigurationService cs = ConfigurationService.getInstance();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getConsoleMessage()));
            return true;
        }
        return false;
    }
}
