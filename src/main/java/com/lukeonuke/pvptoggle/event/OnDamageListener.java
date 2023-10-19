package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.service.PvpService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnDamageListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    //fired when an entity is hit
    public void onHit(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getDamager() instanceof Player)) return;

        if(!PvpService.isPvpEnabled((Player)event.getDamager()) || !PvpService.isPvpEnabled((Player)event.getEntity())){
            event.setCancelled(true);
        }
    }
}
