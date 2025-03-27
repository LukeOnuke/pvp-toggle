package com.lukeonuke.pvptoggle.service;

import com.lukeonuke.pvptoggle.PvpToggle;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Singleton class used for configuration state management and the API for getting/setting configuration fields.
 * Refactored from the static fields that used to "hold" the configuration state in a quite un-OOP manner.
 * @since 3.0.0
 * */
@Getter
public class ConfigurationService {
    private static ConfigurationService instance = null;


    private ConfigurationService() {
    }

    /**
     * Get the instance of <b>ConfigurationService</b>.
     * */
    public static ConfigurationService getInstance() {
        if (instance == null) instance = new ConfigurationService();
        return instance;
    }

    /**
     * Load the configuration file into the <b>ConfigurationService</b> state.
     * @since 3.0.0
     * */
    public void load() {
        final Plugin plugin = PvpToggle.getPlugin();
        FileConfiguration config = plugin.getConfig();

        antiAbuse = config.getBoolean("anti-abuse", true);
        consoleMessage = config.getString("console-message", "§cYou can't use this command from the console.");
        cooldownDuration = config.getInt("cooldown", 120);
        cooldownMessage = config.getString("cooldown-message", "%s of cooldown remaining.");
        deathCooldown = config.getInt("death-cooldown", -1);
        deathMessage = config.getString("death-message", "You are now %s");
        deathStatus = config.getBoolean("death-status", false);
        deathStatusReset = config.getBoolean("death-status-reset", true);
        defaultPvp = config.getBoolean("default-pvp", false);
        disabled = config.getString("disabled", "§aProtected");
        enabled = config.getString("enabled", "§cVulnerable");
        feedbackMessage = plugin.getConfig().getString("pvp-off-message", "You can't fight %s!");
        ffMessage = config.getString("ff-message", "Friendly fire!");
        friendlyFire = config.getBoolean("friendly-fire", false);
        hitSelf = config.getBoolean("hit-self", true);
        limitedMessage = config.getString("limited-message", "You are now %s");
        limitedTime = config.getInt("limited-time", -1);
        notFoundMessage = config.getString("not-found-message", "%s isn't online.");
        permissionMessage = config.getString("permission-message", "You don't have permission to use this command.");
        petPvpMessage = config.getString("pet-pvp-message", "You can't fight %s's %r!");
        prefix = config.getString("prefix", "§4PVP »");
        protectPets = config.getBoolean("protect-pets", true);
        reloadMessage = config.getString("reload-message", "Reloaded!");
        remoteToggleMessage = config.getString("remote-toggle-message", "%s is now %r");
        sendFeedback = config.getBoolean("feedback", false);
        spawnParticles = config.getBoolean("particles", false);
        toggleMessage = config.getString("toggle-message", "You are now %s");
    }

    private Boolean defaultPvp;
    private Integer limitedTime;
    private String limitedMessage;
    private Integer cooldownDuration;
    private String cooldownMessage;
    private String toggleMessage;
    private String consoleMessage;
    private String reloadMessage;
    private String permissionMessage;
    private String notFoundMessage;
    private String remoteToggleMessage;
    private String feedbackMessage;
    private Boolean antiAbuse;
    private Boolean sendFeedback;
    private Boolean hitSelf;
    private Boolean spawnParticles;
    private Boolean protectPets;
    private Boolean friendlyFire;
    private String petPvpMessage;
    private String ffMessage;
    private Boolean deathStatusReset;
    private Boolean deathStatus;
    private Integer deathCooldown;
    private String deathMessage;
    private String prefix;
    private String enabled;
    private String disabled;
}
