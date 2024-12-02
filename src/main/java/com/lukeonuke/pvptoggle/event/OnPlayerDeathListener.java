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
    public static Integer deathCooldown;
    public static Integer cooldownDuration;
    public static String deathMessage;

    private static final NamespacedKey pvpToggledTimestamp = new NamespacedKey(PvpToggle.getPlugin(), "pvpToggledTimestamp");

    @EventHandler()
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (deathStatusReset) {
            PvpService.setPvpEnabled(player, deathStatus);
            if (!deathMessage.isEmpty()) {
                player.sendMessage(ChatFormatterService.addPrefix(deathMessage.replace("%s", ChatFormatterService.booleanHumanReadable(PvpService.isPvpEnabled(player)))));
            }
        }
        if (deathCooldown >= 0) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            dataContainer.set(pvpToggledTimestamp, PersistentDataType.LONG, Instant.now().toEpochMilli() - cooldownDuration * 1000 + deathCooldown * 1000);
        }
    }
}