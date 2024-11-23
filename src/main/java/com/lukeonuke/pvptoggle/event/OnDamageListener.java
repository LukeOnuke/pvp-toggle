package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.PvpToggle;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.PvpService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.*;
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
    public static Boolean protectPets;
    public static Boolean hitPets;
    public static String petPvpMessage;

    public OnDamageListener(String feedbackMessage) {
        OnDamageListener.feedbackMessage = feedbackMessage;
    }

    @EventHandler()
    public void onHit(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Player player;
        Tameable pet = null;
        if (entity instanceof Tameable) {
            pet = (Tameable) entity;
            if (pet.getOwner() instanceof Player) {
                player = (Player) pet.getOwner();
            } else return;
        } else if (!(entity instanceof Player)) return;
        else player = (Player) entity;
        Player damager = null;
        if (event.getDamager() instanceof Player damagerLocal
                && (PvpService.isPvpDisabled(damagerLocal) || PvpService.isPvpDisabled(player))) {
            damager = damagerLocal;
            event.setCancelled(true);
        }

        // If a player hits themselves and hit-self is true, make them take damage.
        // If a player hit their pet and hit-pets is true, make them take damage.
        if (pet != null && damager != null) {
            // if the attacked is a pet, and the attacker is the pet's owner, and you can hit your own pets:
            event.setCancelled(!(damager.equals(player) && hitPets));
        } else {
            // if attacker is a projectile, and the projectile's shooter is a player, and you can hit yourself:
            if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player projectileOwner && hitSelf) {
                damager = projectileOwner;
                // if the attacked player is equal to the attacking player, return.
                if (player.equals(projectileOwner)) return;
                // otherwise, if pvp is disabled for either player, cancel the attack.
                if (PvpService.isPvpDisabled(player)) event.setCancelled(true);
                if (PvpService.isPvpDisabled(projectileOwner)) event.setCancelled(true);
            }
        }

        // If a player is hit by another player, but is protected, spawn particles and send a message to the attacker.
        if (event.isCancelled() && Objects.nonNull(damager)) {
            if (spawnParticles && pet != null) damager.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, pet.getLocation(), 10);
            else damager.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 10);
            if (sendFeedback && pet != null) damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((ChatFormatterService.addPrefix(petPvpMessage.replace("%s", player.getDisplayName() + ChatColor.RESET)))));
            else damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((ChatFormatterService.addPrefix(feedbackMessage.replace("%s", player.getDisplayName() + ChatColor.RESET)))));
        }

        // If a player is hit by another player, is vulnerable, and anti-abuse is true, restart the damaged player's cooldown.
        if (!event.isCancelled() && antiAbuse) {
            PvpService.setPvpCooldownTimestamp(player);
        }
    }
}
