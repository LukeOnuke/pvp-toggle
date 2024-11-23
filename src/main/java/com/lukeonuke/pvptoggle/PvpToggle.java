package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.event.OnDamageListener;
import com.lukeonuke.pvptoggle.event.OnPlayerDeathListener;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.PvpService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PvpToggle extends JavaPlugin {
    @Getter
    public static Plugin plugin = null;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        saveDefaultConfig();
        load();

        Objects.requireNonNull(this.getCommand("pvp")).setExecutor(new PvpCommand());
        Bukkit.getPluginManager().registerEvents(new OnDamageListener(getConfig().getString("pvp-off-message", "You can't fight %s!")), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerDeathListener(), this);
        plugin.getLogger().info("PVPToggle has been registered.");
    }

    public void reload() {
        reloadConfig();
        load();
        plugin.getLogger().info("PVPToggle has been reloaded.");
    }

    private void load() {
        PvpService.defaultPvp = getConfig().getBoolean("default-pvp", false);
        OnDamageListener.antiAbuse = getConfig().getBoolean("anti-abuse", true);
        OnDamageListener.hitSelf = getConfig().getBoolean("hit-self", true);
        OnDamageListener.spawnParticles = getConfig().getBoolean("particles", false);
        OnDamageListener.sendFeedback = getConfig().getBoolean("feedback", false);
        PvpCommand.cooldownDuration = getConfig().getInt("cooldown", 120);
        PvpCommand.cooldownMessage = getConfig().getString("cooldown-message", "%s of cooldown remaining.");
        PvpService.cooldownDuration = getConfig().getInt("cooldown", 120);
        OnPlayerDeathListener.cooldownDuration = getConfig().getInt("cooldown", 120);
        ChatFormatterService.prefix = getConfig().getString("prefix", "§4PVP »");
        OnPlayerDeathListener.deathStatusReset = getConfig().getBoolean("death-status-reset", true);
        OnPlayerDeathListener.deathStatus = getConfig().getBoolean("death-status", false);
        OnPlayerDeathListener.deathCooldownReset = getConfig().getBoolean("death-cooldown-reset", true);
        OnPlayerDeathListener.deathCooldown = getConfig().getInt("death-cooldown", 0);
        OnPlayerDeathListener.deathMessage = getConfig().getBoolean("death-message", true);
        OnDamageListener.protectPets = getConfig().getBoolean("protect-pets", true);
        OnDamageListener.petPvpMessage = getConfig().getString("pet-pvp-message", "You can't fight %s's pet!");
        OnDamageListener.hitPets = getConfig().getBoolean("hit-pets", false);
    }
}
