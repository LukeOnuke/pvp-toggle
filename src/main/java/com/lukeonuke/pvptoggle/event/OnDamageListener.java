package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.service.PvpService;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnDamageListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    //fired when an entity is hit
    public void onHit(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player)event.getDamager();
        if(event.getDamager() instanceof Player) {
            if(!PvpService.isPvpEnabled(player) || !PvpService.isPvpEnabled((Player)event.getEntity())){
                event.setCancelled(true);
            }
        }

        if(event.getDamager() instanceof Projectile){
            if(event.getDamager() instanceof Player projectileOwner) {
                if(!PvpService.isPvpEnabled(player)) event.setCancelled(true);
                if(!PvpService.isPvpEnabled(projectileOwner)) event.setCancelled(true);
            }
        }
    }
}
