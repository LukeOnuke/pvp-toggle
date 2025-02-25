package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.ConfigurationService;
import com.lukeonuke.pvptoggle.service.PvpService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class PvpCommand implements CommandExecutor{

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] args) {
        final ConfigurationService cs = ConfigurationService.getInstance();
        if (args.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getConsoleMessage()));
                return true;
            }

            if (!PvpService.isPvpCooldownDone(player) && PvpService.isPvpEnabled(player)) {
                long remainingCooldownMs = PvpService.getPvpCooldownTimestamp(player).toEpochMilli() + (cs.getCooldownDuration() * 1000 + 1000) - Instant.now().toEpochMilli();
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getCooldownMessage()).replace("%s", ChatFormatterService.formatTime(remainingCooldownMs)));
                return true;
            }

            boolean isPvpEnabled = PvpService.isPvpEnabled(player);
            PvpService.setPvpEnabled(player, !isPvpEnabled);
            commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getToggleMessage()).replace("%s", ChatFormatterService.booleanHumanReadable(isPvpEnabled)));
        }
        else if (args[0].equals("reload")) {
            if (commandSender.hasPermission("pvptoggle.reload")) {
                PvpToggle pluginInstance = (PvpToggle) PvpToggle.plugin;
                pluginInstance.reload();
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getReloadMessage()));
            } else {
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getPermissionMessage()));
            }
        }
        else {
            if (!commandSender.hasPermission("pvptoggle.pvp.others")) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getPermissionMessage()));
                return true;
            }

            for (String arg : args) {
                Player player = Bukkit.getPlayer(arg);
                if (player == null) {
                    commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getNotFoundMessage().replace("%s", args[0])));
                    continue;
                }

                boolean isPvpEnabled = PvpService.isPvpEnabled(player);
                PvpService.setPvpEnabled(player, !isPvpEnabled);
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getRemoteToggleMessage()).replace("%s", player.getName()).replace("%r", ChatFormatterService.booleanHumanReadable(isPvpEnabled)));
            }
        }
        return true;
    }
}
