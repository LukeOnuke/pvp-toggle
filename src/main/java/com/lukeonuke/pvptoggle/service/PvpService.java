package com.lukeonuke.pvptoggle.service;

import com.lukeonuke.pvptoggle.PvpToggle;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.util.Objects;

public class PvpService {
    private static final NamespacedKey isPvpEnabledKey = new NamespacedKey(PvpToggle.getPlugin(), "isPvpEnabled");
    private static final NamespacedKey pvpToggledTimestamp = new NamespacedKey(PvpToggle.getPlugin(), "pvpToggledTimestamp");
    private static Boolean notDefaultPvp;
    
    public static void setDefaultPvp(boolean defaultPvp) {
        PvpService.notDefaultPvp = !defaultPvp;
    }
    
    public static boolean isPvpDisabled(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        // By equating to true we ensure that the data isn't null.
        return notDefaultPvp.equals(dataContainer.get(isPvpEnabledKey, PersistentDataType.BOOLEAN));
    }

    public static void setPvpEnabled(Player player, boolean enabled) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(isPvpEnabledKey, PersistentDataType.BOOLEAN, enabled);
        dataContainer.set(pvpToggledTimestamp, PersistentDataType.LONG, Instant.now().toEpochMilli());
    }

    public static Instant getPvpCooldownTimestamp(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (!dataContainer.has(pvpToggledTimestamp, PersistentDataType.LONG))
            dataContainer.set(pvpToggledTimestamp, PersistentDataType.LONG, 0L);

        return Instant.ofEpochMilli(Objects.requireNonNull(dataContainer.get(pvpToggledTimestamp, PersistentDataType.LONG)));
    }

    public static boolean isPvpCooldownDone(Player player) {
        return Instant.now().isAfter(Instant.ofEpochMilli(getPvpCooldownTimestamp(player).toEpochMilli() + 5000));
    }
}
