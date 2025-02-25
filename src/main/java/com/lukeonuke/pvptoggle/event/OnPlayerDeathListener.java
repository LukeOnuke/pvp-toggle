package com.lukeonuke.pvptoggle.event;

import com.lukeonuke.pvptoggle.PvpToggle;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.ConfigurationService;
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

    private static final NamespacedKey pvpToggledTimestamp = new NamespacedKey(PvpToggle.getPlugin(), "pvpToggledTimestamp");

    @EventHandler()
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        final ConfigurationService cs = ConfigurationService.getInstance();
        if (cs.getDeathStatusReset()) {
            PvpService.setPvpEnabled(player, cs.getDeathStatus());
            if (!cs.getDeathMessage().isEmpty()) {
                player.sendMessage(ChatFormatterService.addPrefix(cs.getDeathMessage().replace("%s", ChatFormatterService.booleanHumanReadable(PvpService.isPvpEnabled(player)))));
            }
        }
        if (cs.getDeathCooldown() >= 0) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            dataContainer.set(pvpToggledTimestamp, PersistentDataType.LONG, Instant.now().toEpochMilli() - cs.getCooldownDuration() * 1000 + cs.getDeathCooldown() * 1000);
        }
    }
}