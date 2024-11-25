package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.service.PvpService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PvpService.handlePlayerJoin(event.getPlayer());
    }
}
