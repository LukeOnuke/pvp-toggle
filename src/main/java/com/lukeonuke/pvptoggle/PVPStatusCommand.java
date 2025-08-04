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

public class PVPStatusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final ConfigurationService cs = ConfigurationService.getInstance();

        Player player;
        if(args.length < 1){
            if (commandSender instanceof Player playerCommandSender) {
                player = playerCommandSender;
            }else {
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getConsoleMessage()));
                return true;
            }
        }else {
            if (!commandSender.hasPermission("pvptoggle.pvpstatus.others")){
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getPermissionMessage()));
            }

            player = Bukkit.getPlayerExact(args[0]);

            if(player == null){
                commandSender.sendMessage(ChatFormatterService.addPrefix(String.format(cs.getNotFoundMessage(), args[0])));
                return true;
            }
        }
        commandSender.sendMessage(
                ChatFormatterService.addPrefix(
                        cs.getPvpStatusMessage()
                                .replace("%s", player.getDisplayName())
                                .replace("%r", ChatFormatterService.booleanHumanReadable(PvpService.isPvpEnabled(player)))
                )
        );
        return true;
    }
}
