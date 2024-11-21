package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.PvpToggle;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.PvpService;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;

public class OnPlayerDeathListener implements Listener {
    public static Boolean deathStatusReset;
    public static Boolean deathStatus;
    public static Boolean deathCooldownReset;
    public static Integer deathCooldown;
    public static Integer cooldownDuration;
    public static Boolean deathMessage;

    private static final NamespacedKey pvpToggledTimestamp = new NamespacedKey(PvpToggle.getPlugin(), "pvpToggledTimestamp");

    @EventHandler()
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (deathStatusReset) {
            PvpService.setPvpEnabled(player, deathStatus);
            if (deathMessage) {
                boolean isPvpEnabled = PvpService.isPvpDisabled(player);
                player.sendMessage(ChatFormatterService.addPrefix("You are now " + ChatFormatterService.booleanHumanReadable(!isPvpEnabled)));
            }
        }
        if (deathCooldownReset) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            dataContainer.set(pvpToggledTimestamp, PersistentDataType.LONG, Instant.now().toEpochMilli() - cooldownDuration * 1000 + deathCooldown * 1000);
        }
    }
}