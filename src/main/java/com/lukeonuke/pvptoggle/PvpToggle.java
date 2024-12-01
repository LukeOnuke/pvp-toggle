package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.event.OnDamageListener;
import com.lukeonuke.pvptoggle.event.OnPlayerDeathListener;
import com.lukeonuke.pvptoggle.event.OnPlayerJoin;
import com.lukeonuke.pvptoggle.event.OnPlayerQuit;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.PlaceholderExpansionService;
import com.lukeonuke.pvptoggle.service.PvpService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PvpToggle extends JavaPlugin {
    @Getter
    public static Plugin plugin = null;
    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        load();

        Objects.requireNonNull(this.getCommand("pvp")).setExecutor(new PvpCommand());
        Bukkit.getPluginManager().registerEvents(new OnDamageListener(getConfig().getString("pvp-off-message", "You can't fight %s!")), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderExpansionService().register();
        }

        plugin.getLogger().info("PVPToggle has been registered. o/");
    }

    public void onDisable() {
        PvpService.shutdown();
    }

    public void reload() {
        reloadConfig();
        load();
        plugin.getLogger().info("PVPToggle has been reloaded.");
    }

    private void load() {
        FileConfiguration config = getConfig();

        PvpService.defaultPvp = config.getBoolean("default-pvp", false);
        OnDamageListener.antiAbuse = config.getBoolean("anti-abuse", true);
        OnDamageListener.hitSelf = config.getBoolean("hit-self", true);
        OnDamageListener.spawnParticles = config.getBoolean("particles", false);
        OnDamageListener.sendFeedback = config.getBoolean("feedback", false);
        PvpCommand.cooldownDuration = config.getInt("cooldown", 120);
        PvpCommand.cooldownMessage = config.getString("cooldown-message", "%s of cooldown remaining.");

        PvpCommand.toggleMessage = config.getString("toggle-message", "You are now %s");
        PvpCommand.consoleMessage = config.getString("console-message", "§cYou can't use this command from the console.");
        PvpCommand.permissionMessage = config.getString("permission-message", "You don't have permission to use this command.");
        PvpCommand.reloadMessage = config.getString("reload-message", "Reloaded!");
        PvpCommand.notFoundMessage = config.getString("not-found-message", "%s isn't online.");
        PvpCommand.remoteToggleMessage = config.getString("remote-toggle-message", "%s is now %r");

        PvpService.cooldownDuration = config.getInt("cooldown", 120);
        PvpService.limitedTime = config.getInt("limited-time", -1);
        PvpService.limitedMessage = config.getString("limited-message", "You are now %s");
        OnPlayerDeathListener.cooldownDuration = config.getInt("cooldown", 120);
        ChatFormatterService.prefix = config.getString("prefix", "§4PVP »");
        ChatFormatterService.enabled = config.getString("enabled", "§cVulnerable");
        ChatFormatterService.disabled = config.getString("disabled", "§aProtected");
        OnPlayerDeathListener.deathStatusReset = config.getBoolean("death-status-reset", true);
        OnPlayerDeathListener.deathStatus = config.getBoolean("death-status", false);
        OnPlayerDeathListener.deathCooldown = config.getInt("death-cooldown", -1);
        OnPlayerDeathListener.deathMessage = config.getString("death-message", "You are now %s");
        OnDamageListener.protectPets = config.getBoolean("protect-pets", true);
        OnDamageListener.petPvpMessage = config.getString("pet-pvp-message", "You can't fight %s's %r!");
        OnDamageListener.friendlyFire = config.getBoolean("friendly-fire", false);
        OnDamageListener.ffMessage = config.getString("ff-message", "Friendly fire!");
    }
}
