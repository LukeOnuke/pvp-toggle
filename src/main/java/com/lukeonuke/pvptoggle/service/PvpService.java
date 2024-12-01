package com.lukeonuke.pvptoggle.service;

import com.lukeonuke.pvptoggle.PvpToggle;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.*;

public class PvpService {
    private static final NamespacedKey isPvpEnabledKey = new NamespacedKey(PvpToggle.getPlugin(), "isPvpEnabled");
    private static final NamespacedKey pvpToggledTimestamp = new NamespacedKey(PvpToggle.getPlugin(), "pvpToggledTimestamp");
    public static Boolean defaultPvp;
    public static Integer limitedTime;
    public static String limitedMessage;
    public static Integer cooldownDuration;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final ConcurrentHashMap<Player, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> expirationTimes = new ConcurrentHashMap<>();

    public static boolean isPvpDisabled(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        // Mr. Pier, this is a way of persistent storage of player states.
        return !defaultPvp.equals(dataContainer.get(isPvpEnabledKey, PersistentDataType.BOOLEAN));
    }

    public static void setPvpEnabled(Player player, boolean enabled) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(isPvpEnabledKey, PersistentDataType.BOOLEAN, !enabled);
        dataContainer.set(pvpToggledTimestamp, PersistentDataType.LONG, Instant.now().toEpochMilli());

        if (limitedTime < 0) return;
        if (enabled) {
            // pvp enabled, remove any existing task
            activeTasks.remove(player);
        } else {
            // pvp disabled, schedule a task to re-enable it after the timer
            ScheduledFuture<?> existingTask = activeTasks.get(player);
            if (existingTask != null) {
                existingTask.cancel(false);
            }

            Runnable task = () -> {
                if (player.isOnline()) {
                    setPvpEnabled(player, true);
                    if (!limitedMessage.isEmpty()) {
                        player.sendMessage(ChatFormatterService.addPrefix(
                                limitedMessage.replace("%s", ChatFormatterService.booleanHumanReadable(true))));
                    }
                } else {
                    resetPvpStatus(player);
                }
            };

            ScheduledFuture<?> scheduledTask = scheduler.schedule(task, limitedTime, TimeUnit.SECONDS);
            activeTasks.put(player, scheduledTask);
        }
    }

    public static void handlePlayerLeave(Player player) {
        ScheduledFuture<?> existingTask = activeTasks.remove(player);
        if (existingTask != null) {
            existingTask.cancel(false);
        }
        expirationTimes.put(player.getUniqueId().toString(), System.currentTimeMillis() + limitedTime * 1000);
    }

    public static void handlePlayerJoin(Player player) {
        String uuid = player.getUniqueId().toString();
        Long expirationTime = expirationTimes.get(uuid);

        if (limitedTime < 0) {
            // if limited-time is disabled, do nothing and keep the current pvp state
            return;
        }

        if (expirationTime != null) {
            if (System.currentTimeMillis() > expirationTime) {
                setPvpEnabled(player, true); // Protection expired, set to vulnerable
                expirationTimes.remove(uuid);
                player.sendMessage(ChatFormatterService.addPrefix(
                        limitedMessage.replace("%s", ChatFormatterService.booleanHumanReadable(true))));
            } else {
                setPvpEnabled(player, false); // still within protection window, remain protected
            }
        } else {
            resetPvpStatus(player); // no record found, reset to default
        }
    }


    private static void resetPvpStatus(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(isPvpEnabledKey, PersistentDataType.BOOLEAN, defaultPvp);
    }

    public static void setPvpCooldownTimestamp(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(pvpToggledTimestamp, PersistentDataType.LONG, Instant.now().toEpochMilli());
    }

    public static Instant getPvpCooldownTimestamp(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (!dataContainer.has(pvpToggledTimestamp, PersistentDataType.LONG))
            dataContainer.set(pvpToggledTimestamp, PersistentDataType.LONG, 0L);

        return Instant.ofEpochMilli(Objects.requireNonNull(dataContainer.get(pvpToggledTimestamp, PersistentDataType.LONG)));
    }

    public static boolean isPvpCooldownDone(Player player) {
        return Instant.now().isAfter(Instant.ofEpochMilli(getPvpCooldownTimestamp(player).toEpochMilli() + cooldownDuration * 1000));
    }

    public static void shutdown() {
        scheduler.shutdown();
        activeTasks.clear();
        expirationTimes.clear();
    }
}
