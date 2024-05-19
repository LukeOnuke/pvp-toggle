package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.event.OnDamageListener;
import com.lukeonuke.pvptoggle.service.PvpService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PvpToggle extends JavaPlugin {
    @Getter
    private static Plugin plugin = null;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        saveDefaultConfig();
        PvpService.setDefaultPvp(getConfig().getBoolean("default-pvp", false));
        Objects.requireNonNull(this.getCommand("pvp")).setExecutor(new PvpCommand());
        Bukkit.getPluginManager().registerEvents(new OnDamageListener(getConfig().getString("pvp-off-message", "You can't PVP with %s!")), this);
        plugin.getLogger().info("Registered successfully!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
