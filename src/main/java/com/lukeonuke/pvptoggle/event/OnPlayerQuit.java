package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.service.PvpService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuit implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PvpService.handlePlayerLeave(event.getPlayer());
    }
}
