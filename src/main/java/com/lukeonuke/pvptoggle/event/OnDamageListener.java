package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.PvpToggle;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.PvpService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Objects;

public class OnDamageListener implements Listener {
    @EventHandler()
    //fired when an entity is hit
    public void onHit(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        Player damager = null;
        if(event.getDamager() instanceof Player damagerLocal) {
            if(!PvpService.isPvpEnabled(damagerLocal) || !PvpService.isPvpEnabled(player)){
                damager = damagerLocal;
                event.setCancelled(true);
            }
        }

        if(event.getDamager() instanceof Projectile projectile){
            if(projectile.getShooter() instanceof Player projectileOwner) {
                damager = projectileOwner;
                if(player.equals(projectileOwner)) return; //can shoot yourself
                if(!PvpService.isPvpEnabled(player)) event.setCancelled(true);
                if(!PvpService.isPvpEnabled(projectileOwner)) event.setCancelled(true);
            }
        }

        if(event.isCancelled()){
            if(Objects.nonNull(damager)) {
                damager.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 10);
                damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((ChatFormatterService.addPrefix("You can't PVP with " + player.getDisplayName() + ChatColor.RESET + "!" ))));
            }
        }
    }
}
