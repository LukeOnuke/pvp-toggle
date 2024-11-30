package com.lukeonuke.pvptoggle.event;

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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OnDamageListener implements Listener {
    public static String feedbackMessage;
    public static Boolean antiAbuse;
    public static Boolean sendFeedback;
    public static Boolean hitSelf;
    public static Boolean spawnParticles;
    public static Boolean protectPets;
    public static Boolean friendlyFire;
    public static String petPvpMessage;
    public static String ffMessage;

    public OnDamageListener(String feedbackMessage) {
        OnDamageListener.feedbackMessage = feedbackMessage;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Player player;
        Tameable pet = null;

        // Determine if the entity is a pet and retrieve the owner
        if (entity instanceof Tameable) {
            pet = (Tameable) entity;
            if (pet.getOwner() instanceof Player) {
                player = (Player) pet.getOwner();
            } else return;
        } else if (!(entity instanceof Player)) return;
        else player = (Player) entity;

        Player damager = null;

        // Check for direct melee attack
        if (event.getDamager() instanceof Player damagerLocal) {
            damager = damagerLocal;

            // Cancel PvP if either player has it disabled
            if (PvpService.isPvpDisabled(damager) || PvpService.isPvpDisabled(player)) {
                event.setCancelled(true);
            }

            // Cancel attack on pet if friendlyFire is off and damager is the pet's owner
            if (pet != null && !friendlyFire && damager.equals(player)) {
                event.setCancelled(true);
            }
        }

        // Check for projectile attack
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            damager = shooter;

            // Cancel attack on own pet if friendlyFire is off
            if (pet != null && !friendlyFire && damager.equals(player)) {
                event.setCancelled(true);
            }

            // Cancel if PvP is disabled for either player
            if (!((damager == player || event.getDamageSource().getCausingEntity() == player) && hitSelf)) {
                if (PvpService.isPvpDisabled(player) || PvpService.isPvpDisabled(damager)) {
                    event.setCancelled(true);
                }
            }
        }

        // Check for other attacks such as TNT
        if (event.getDamageSource().getCausingEntity() instanceof Player cause) {
            // Cancel if PvP is disabled for either player
            if (!((damager == player || event.getDamageSource().getCausingEntity() == player) && hitSelf)) {
                if (PvpService.isPvpDisabled(player) || PvpService.isPvpDisabled(cause)) {
                    event.setCancelled(true);
                    if (spawnParticles) {
                        cause.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, Objects.requireNonNullElse(pet, player).getLocation(), 10);
                    }
                    if (sendFeedback) {
                        TextComponent actionBarMessage = getActionBarMessage(pet, player, cause);
                        cause.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
                    }
                    if (!event.isCancelled() && antiAbuse) {
                        PvpService.setPvpCooldownTimestamp(player);
                    }
                    return;
                }
            }

            // Cancel attack on own pet if friendlyFire is off
            if (pet != null && !friendlyFire && (damager == player || event.getDamageSource().getCausingEntity() == player)) {
                event.setCancelled(true);
                if (spawnParticles) {
                    cause.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, pet.getLocation(), 10);
                }
                if (sendFeedback) {
                    TextComponent actionBarMessage = getActionBarMessage(pet, player, cause);
                    cause.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
                }
                if (!event.isCancelled() && antiAbuse) {
                    PvpService.setPvpCooldownTimestamp(player);
                }
                return;
            }
        }

        // Check if attacked by a tamable entity
        if (event.getDamager() instanceof Tameable tamableAttacker) {
            if (tamableAttacker.getOwner() instanceof Player attackerOwner) {
                // Cancel if PvP is disabled for either the player or the owner of the tamable attacker
                if (PvpService.isPvpDisabled(player) || PvpService.isPvpDisabled(attackerOwner)) {
                    event.setCancelled(true);
                    if (tamableAttacker instanceof Wolf wolf) {
                        wolf.setAngry(false);
                    }
                }
            }
        }

        // If attack is cancelled, handle feedback and particles
        if (event.isCancelled() && damager != null) {
            if (spawnParticles) {
                damager.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, Objects.requireNonNullElse(pet, player).getLocation(), 10);
            }

            // Send feedback message to the attacker
            if (sendFeedback) {
                TextComponent actionBarMessage = getActionBarMessage(pet, player, damager);
                damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
            }
        }

        // Handle anti-abuse cooldown if attack is not cancelled
        if (!event.isCancelled() && antiAbuse) {
            if(Objects.isNull(player)) return;
            PvpService.setPvpCooldownTimestamp(player);
        }
    }

    private static @NotNull TextComponent getActionBarMessage(Tameable pet, Player player, Player damager) {
        String message;
        if (pet != null) {
            if (damager.equals(player)) message = ChatFormatterService.addPrefix(ffMessage);
            else message = ChatFormatterService.addPrefix(
                    petPvpMessage.replace("%s", player.getDisplayName() + ChatColor.RESET).replace("%r", pet.getName())
            );
        } else {
            message = ChatFormatterService.addPrefix(
                    feedbackMessage.replace("%s", player.getDisplayName() + ChatColor.RESET)
            );
        }
        return new TextComponent(message);
    }
}
