package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.PvpService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class PvpCommand implements CommandExecutor{
    public static Integer cooldownDuration;
    public static String cooldownMessage;
    public static String toggleMessage;
    public static String consoleMessage;
    public static String reloadMessage;
    public static String permissionMessage;
    public static String notFoundMessage;
    public static String remoteToggleMessage;

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(consoleMessage));
                return true;
            }

            if (!PvpService.isPvpCooldownDone(player) && !PvpService.isPvpDisabled(player)) {
                long remainingCooldownMs = PvpService.getPvpCooldownTimestamp(player).toEpochMilli() + (cooldownDuration * 1000 + 1000) - Instant.now().toEpochMilli();
                commandSender.sendMessage(ChatFormatterService.addPrefix(cooldownMessage.replace("%s", ChatFormatterService.formatTime(remainingCooldownMs))));
                return true;
            }

            boolean isPvpEnabled = PvpService.isPvpDisabled(player);
            PvpService.setPvpEnabled(player, isPvpEnabled);
            commandSender.sendMessage(ChatFormatterService.addPrefix(toggleMessage.replace("%s", ChatFormatterService.booleanHumanReadable(isPvpEnabled))));
        }
        else if (args[0].equals("reload")) {
            if (commandSender.hasPermission("pvptoggle.reload")) {
                PvpToggle pluginInstance = (PvpToggle) PvpToggle.plugin;
                pluginInstance.reload();
                commandSender.sendMessage(ChatFormatterService.addPrefix(reloadMessage));
            } else {
                commandSender.sendMessage(ChatFormatterService.addPrefix(permissionMessage));
            }
        }
        else {
            if (!commandSender.hasPermission("pvptoggle.pvp.others")) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(permissionMessage));
                return true;
            }

            for (String arg : args) {
                Player player = Bukkit.getPlayer(arg);
                if (player == null) {
                    commandSender.sendMessage(ChatFormatterService.addPrefix(notFoundMessage.replace("%s", args[0])));
                    continue;
                }

                boolean isPvpEnabled = PvpService.isPvpDisabled(player);
                PvpService.setPvpEnabled(player, isPvpEnabled);
                commandSender.sendMessage(ChatFormatterService.addPrefix(remoteToggleMessage.replace("%s", player.getName()).replace("%r", ChatFormatterService.booleanHumanReadable(isPvpEnabled))));
            }
        }
        return true;
    }
}
