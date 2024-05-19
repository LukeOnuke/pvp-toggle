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
    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender, 
            @NotNull Command command,
            @NotNull String s,
            String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(ChatColor.RED + "" + ChatColor.BOLD + "You cant use this command from the console!"));
                return true;
            }

            if (!PvpService.isPvpCooldownDone(player)) {
                commandSender.sendMessage(
                    ChatFormatterService.addPrefix(
                    (PvpService.getPvpCooldownTimestamp(player).toEpochMilli() + 5000 - Instant.now().toEpochMilli()) / 1000 + "s of cooldown left."
                    )
                );
                return true;
            }

            boolean isPvpEnabled = PvpService.isPvpDisabled(player);
            PvpService.setPvpEnabled(player, isPvpEnabled);
            commandSender.sendMessage(ChatFormatterService.addPrefix("PVP is now " + ChatFormatterService.booleanHumanReadable(isPvpEnabled) ));
        } else {
            if (!commandSender.hasPermission("pvptoggle.pvp.others")) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(ChatColor.RED + "" + ChatColor.BOLD + "You don't have the permission for this."));
                return true;
            }
            
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(ChatColor.RED + "" + ChatColor.BOLD + "Cant find player " + args[0] + "."));
                return true;
            }
            
            boolean isPvpEnabled = PvpService.isPvpDisabled(player);
            PvpService.setPvpEnabled(player, isPvpEnabled);
            commandSender.sendMessage(ChatFormatterService.addPrefix("PVP for " + player.getName() + " is now " + ChatFormatterService.booleanHumanReadable(isPvpEnabled)));
        }
        return true;
    }
}
