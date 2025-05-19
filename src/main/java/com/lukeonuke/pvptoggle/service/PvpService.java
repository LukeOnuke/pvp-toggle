package com.lukeonuke.pvptoggle.service;

import com.lukeonuke.pvptoggle.PvpToggle;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Service class containing methods for manipulation of player vulnerability to pvp.
 */
public class PvpService {
    private static final NamespacedKey isPvpEnabledKey = new NamespacedKey(PvpToggle.getPlugin(), "isPvpEnabled");
    private static final NamespacedKey pvpToggledTimestampKey = new NamespacedKey(PvpToggle.getPlugin(), "pvpToggledTimestamp");
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final ConcurrentHashMap<Player, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> expirationTimes = new ConcurrentHashMap<>();

    /**
     * Returns if the player has pvp enabled.
     * @param player The player to check for pvp.
     * @return <b>TRUE</b> if pvp is enabled and <b>FALSE</b> if pvp isn't enabled.
     */
    public static boolean isPvpEnabled(@NotNull Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        // Mr. Pier, this is a way of persistent storage of player states.
        // Mr. Kresoja, this is severely scuffed.
        return Boolean.TRUE.equals(dataContainer.get(isPvpEnabledKey, PersistentDataType.BOOLEAN));
    }

    /**
     * Set player's pvp enabled status.
     * @param player Player that is going to get his pvp set.
     * @param enabled State of pvp to set.
     */
    public static void setPvpEnabled(Player player, boolean enabled) {
        final ConfigurationService configurationService = ConfigurationService.getInstance();

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(isPvpEnabledKey, PersistentDataType.BOOLEAN, enabled);
        dataContainer.set(pvpToggledTimestampKey, PersistentDataType.LONG, Instant.now().toEpochMilli());

        if (configurationService.getLimitedTime() < 0) return;

        if (enabled) {
            // pvp enabled, cancel any existing task
            ScheduledFuture<?> existingTask = activeTasks.remove(player);
            if (existingTask != null) {
                existingTask.cancel(false); // cancel the task
            }
        } else {
            // pvp disabled, schedule a task to re-enable it after the timer
            ScheduledFuture<?> existingTask = activeTasks.get(player);
            if (existingTask != null) {
                existingTask.cancel(false);
            }

            Runnable task = () -> {
                if (player.isOnline()) {
                    setPvpEnabled(player, true);
                    if (!configurationService.getLimitedMessage().isEmpty()) {
                        player.sendMessage(ChatFormatterService.addPrefix(
                                configurationService.getLimitedMessage().replace("%s",
                                        ChatFormatterService.booleanHumanReadable(false))));
                    }
                } else {
                    resetPvpStatus(player);
                }
            };

            ScheduledFuture<?> scheduledTask = scheduler.schedule(task, configurationService.getLimitedTime(), TimeUnit.SECONDS);
            activeTasks.put(player, scheduledTask);
        }
    }


    /**
     * Properly handles players leaving.
     * @param player The player that has left.
     */
    public static void handlePlayerLeave(Player player) {
        final ConfigurationService configurationService = ConfigurationService.getInstance();

        ScheduledFuture<?> existingTask = activeTasks.remove(player);
        if (existingTask != null) {
            existingTask.cancel(false);
        }
        expirationTimes.put(player.getUniqueId().toString(), System.currentTimeMillis() + configurationService.getLimitedTime() * 1000);
    }

    /**
     * Properly handles players entering the game.
     * @param player The player that has joined.
     */
    public static void handlePlayerJoin(Player player) {
        final ConfigurationService configurationService = ConfigurationService.getInstance();

        String uuid = player.getUniqueId().toString();
        Long expirationTime = expirationTimes.get(uuid);

        if (configurationService.getLimitedTime() < 0) {
            // if limited-time is disabled, do nothing and keep the current pvp state
            return;
        }

        if (expirationTime != null) {
            if (System.currentTimeMillis() > expirationTime) {
                setPvpEnabled(player, true);
                if (!configurationService.getLimitedMessage().isEmpty()) {
                    player.sendMessage(ChatFormatterService.addPrefix(
                            configurationService.getLimitedMessage().replace("%s",
                                    ChatFormatterService.booleanHumanReadable(false))));
                }
            } else {
                setPvpEnabled(player, false); // still within protection window, remain protected
            }
        } else {
            resetPvpStatus(player); // no record found, reset to default
        }
    }


    /**
     * Resets players pvp enabled status to configuration default.
     * @param player The player that will have their pvp reset.
     */
    private static void resetPvpStatus(Player player) {
        final ConfigurationService configurationService = ConfigurationService.getInstance();
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(isPvpEnabledKey, PersistentDataType.BOOLEAN, configurationService.getDefaultPvp());
    }

    /**
     * Sets players pvp cooldown timestamp. Used to keep track when was the last time pvp was manually toggled
     * @param player The player that will have their timestamp set.
     */
    public static void setPvpCooldownTimestamp(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(pvpToggledTimestampKey, PersistentDataType.LONG, Instant.now().toEpochMilli());
    }

    /**
     * Gets players pvp cooldown timestamp. Used to keep track when was the last time pvp was manually toggled
     * @param player The player that will have their timestamp retrieved.
     */
    public static Instant getPvpCooldownTimestamp(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (!dataContainer.has(pvpToggledTimestampKey, PersistentDataType.LONG))
            dataContainer.set(pvpToggledTimestampKey, PersistentDataType.LONG, 0L);

        return Instant.ofEpochMilli(Objects.requireNonNull(dataContainer.get(pvpToggledTimestampKey, PersistentDataType.LONG)));
    }

    /**
     * Returns boolean on weather the players cooldown for toggling pvp is over.
     * @param player The player that will have their pvp cooldown checked.
     * @return <b>TRUE</b> if players cooldown has ended, <b>FALSE</b> otherwise.
     */
    public static boolean isPvpCooldownDone(Player player) {
        final ConfigurationService configurationService = ConfigurationService.getInstance();
        return Instant.now().isAfter(Instant.ofEpochMilli(getPvpCooldownTimestamp(player).toEpochMilli() + configurationService.getCooldownDuration() * 1000));
    }

    /**
     * Properly shut down PVP service.
     */
    public static void shutdown() {
        scheduler.shutdown();
        activeTasks.clear();
        expirationTimes.clear();
    }
}
