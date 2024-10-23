package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.PvpToggle;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.PvpService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Objects;

public class OnDamageListener implements Listener {
    public static String feedbackMessage;
    public static Boolean antiAbuse;
    public static Boolean sendFeedback;
    public static Boolean hitSelf;
    public static Boolean spawnParticles;

    public OnDamageListener(String feedbackMessage) {
        OnDamageListener.feedbackMessage = feedbackMessage;
    }

    @EventHandler()
    public void onHit(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        Player damager = null;
        if (event.getDamager() instanceof Player damagerLocal
                && (PvpService.isPvpDisabled(damagerLocal) || PvpService.isPvpDisabled(player))) {
            damager = damagerLocal;
            event.setCancelled(true);
        }

        // If a player hits themselves and hit-self is true, make them take damage.
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player projectileOwner && hitSelf) {
            damager = projectileOwner;
            if (player.equals(projectileOwner)) return;
            if (PvpService.isPvpDisabled(player)) event.setCancelled(true);
            if (PvpService.isPvpDisabled(projectileOwner)) event.setCancelled(true);
        }

        // If a player is hit by another player, but is protected, spawn particles and send a message to the attacker.
        if (event.isCancelled() && Objects.nonNull(damager)) {
            if (spawnParticles) damager.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 10);
            if (sendFeedback) damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((ChatFormatterService.addPrefix(feedbackMessage.replace("%s", player.getDisplayName() + ChatColor.RESET)))));
        }

        // If a player is hit by another player, is vulnerable, and anti-abuse is true, restart the damaged player's cooldown.
        if (!event.isCancelled() && antiAbuse) {
            PvpService.setPvpCooldownTimestamp(player);
        }
    }
}
