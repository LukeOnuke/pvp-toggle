package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.event.OnDamageListener;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.PvpService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        PvpService.defaultPvp = getConfig().getBoolean("default-pvp", false);
        OnDamageListener.antiAbuse = getConfig().getBoolean("anti-abuse", true);
        OnDamageListener.hitSelf = getConfig().getBoolean("hit-self", true);
        OnDamageListener.spawnParticles = getConfig().getBoolean("particles", false);
        OnDamageListener.sendFeedback = getConfig().getBoolean("feedback", false);
        PvpCommand.cooldownDuration = getConfig().getInt("cooldown", 120);
        PvpCommand.cooldownMessage = getConfig().getString("cooldown-message", "%s of cooldown remaining.")
        PvpService.cooldownDuration = getConfig().getInt("cooldown", 120);
        ChatFormatterService.prefix = getConfig().getString("prefix", "§4PVP »");

        Objects.requireNonNull(this.getCommand("pvp")).setExecutor(new PvpCommand());
        Bukkit.getPluginManager().registerEvents(new OnDamageListener(getConfig().getString("pvp-off-message", "You can't fight %s!")), this);
        plugin.getLogger().info("PVPToggle has been registered.");
    }

    public void reload() {
        reloadConfig();
        PvpService.defaultPvp = getConfig().getBoolean("default-pvp", false);
        OnDamageListener.antiAbuse = getConfig().getBoolean("anti-abuse", true);
        OnDamageListener.hitSelf = getConfig().getBoolean("hit-self", true);
        OnDamageListener.spawnParticles = getConfig().getBoolean("particles", false);
        OnDamageListener.sendFeedback = getConfig().getBoolean("feedback", false);
        PvpCommand.cooldownDuration = getConfig().getInt("cooldown", 120);
        PvpService.cooldownDuration = getConfig().getInt("cooldown", 120);
        ChatFormatterService.prefix = getConfig().getString("prefix", "§4PVP »");

        plugin.getLogger().info("PVPToggle has been reloaded.");
    }
}
