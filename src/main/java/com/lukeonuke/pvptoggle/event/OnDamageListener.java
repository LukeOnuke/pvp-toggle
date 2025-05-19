package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.ConfigurationService;
import com.lukeonuke.pvptoggle.service.PvpService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class OnDamageListener implements Listener {

    public OnDamageListener() {
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Player player;
        Player damager = null;
        Tameable pet = null;
        final ConfigurationService cs = ConfigurationService.getInstance();

        // Determine if the entity is a pet and retrieve the owner
        if (entity instanceof Tameable) {
            pet = (Tameable) entity;
            if (pet.getOwner() instanceof Player) {
                player = (Player) pet.getOwner();
            } else return;
        } else if (!(entity instanceof Player)) return;
        else player = (Player) entity;

        // Check for direct melee attack
        if (event.getDamager() instanceof Player damagerLocal) {
            damager = damagerLocal;

            // Cancel PvP if either player has it disabled
            if (!PvpService.isPvpEnabled(damager) || !PvpService.isPvpEnabled(player)) {
                event.setCancelled(true);
            }

            // Cancel attack on pet if friendlyFire is off and damager is the pet's owner
            if (pet != null && !cs.getFriendlyFire() && damager.equals(player)) {
                event.setCancelled(true);
            }
        }

        // Check for projectile attack
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            damager = shooter;

            // Cancel attack on own pet if friendlyFire is off
            if (pet != null && !cs.getFriendlyFire() && damager.equals(player)) {
                event.setCancelled(true);
            }

            // Cancel if PvP is disabled for either player
            if (!((damager == player || event.getDamageSource().getCausingEntity() == player) && cs.getHitSelf())) {
                if (!PvpService.isPvpEnabled(player) || !PvpService.isPvpEnabled(damager)) {
                    event.setCancelled(true);
                }
            }
        }

        // Check for other attacks such as TNT
        if (event.getDamageSource().getCausingEntity() instanceof Player cause) {
            // Cancel if PvP is disabled for either player
            if (!((damager == player || event.getDamageSource().getCausingEntity() == player) && cs.getHitSelf())) {
                if (!PvpService.isPvpEnabled(player) || !PvpService.isPvpEnabled(cause)) {
                    event.setCancelled(true);
                    // Huh, why are the particles and feedback handled twice, should probably figure that out.
                    // -lukeonuke 19/5/2025
                    if (cs.getSpawnParticles()) {
                        cause.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, Objects.requireNonNullElse(pet, player).getLocation(), 10);
                    }
                    if (cs.getSendFeedback()) {
                        TextComponent actionBarMessage = getActionBarMessage(pet, player, cause);
                        cause.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
                    }
                    if (!event.isCancelled() && cs.getAntiAbuse()) {
                        PvpService.setPvpCooldownTimestamp(player);
                    }
                    return;
                }
            }

            // Cancel attack on own pet if friendlyFire is off
            if (pet != null && !cs.getFriendlyFire() && (damager == player || event.getDamageSource().getCausingEntity() == player)) {
                event.setCancelled(true);
                if (cs.getSpawnParticles()) {
                    cause.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, pet.getLocation(), 10);
                }
                if (cs.getSendFeedback()) {
                    TextComponent actionBarMessage = getActionBarMessage(pet, player, cause);
                    cause.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
                }
                if (!event.isCancelled() && cs.getAntiAbuse()) {
                    PvpService.setPvpCooldownTimestamp(player);
                }
                return;
            }
        }

        // Check if attacked by a tamable entity
        if (event.getDamager() instanceof Tameable tamableAttacker) {
            if (tamableAttacker.getOwner() instanceof Player attackerOwner) {
                // Cancel if PvP is disabled for either the player or the owner of the tamable attacker
                if (!PvpService.isPvpEnabled(player) || !PvpService.isPvpEnabled(attackerOwner)) {
                    event.setCancelled(true);
                    if (tamableAttacker instanceof Wolf wolf) {
                        wolf.setAngry(false);
                    }
                }
            }
        }

        // If attack is cancelled, handle feedback and particles
        if (event.isCancelled() && damager != null) {
            if (cs.getSpawnParticles()) {
                damager.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, Objects.requireNonNullElse(pet, player).getLocation(), 10);
            }

            // Send feedback message to the attacker
            if (cs.getSendFeedback()) {
                TextComponent actionBarMessage = getActionBarMessage(pet, player, damager);
                damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
            }
        }

        // Handle anti-abuse cooldown if attack is not cancelled
        if (!event.isCancelled() && cs.getAntiAbuse()) {
            if(Objects.isNull(player)) return;
            PvpService.setPvpCooldownTimestamp(player);
        }
    }

    private static @NotNull TextComponent getActionBarMessage(Tameable pet, Player player, Player damager) {
        String message;
        final ConfigurationService cs = ConfigurationService.getInstance();
        if (pet != null) {
            if (damager.equals(player)) message = ChatFormatterService.addPrefix(cs.getFfMessage());
            else message = ChatFormatterService.addPrefix(
                    cs.getPetPvpMessage().replace("%s", player.getDisplayName() + ChatColor.RESET).replace("%r", pet.getName())
            );
        } else {
            message = ChatFormatterService.addPrefix(
                    cs.getFeedbackMessage().replace("%s", player.getDisplayName() + ChatColor.RESET)
            );
        }
        return new TextComponent(message);
    }
}
