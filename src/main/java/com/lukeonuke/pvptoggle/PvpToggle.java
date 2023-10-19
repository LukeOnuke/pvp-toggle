package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.event.OnDamageListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class PvpToggle extends JavaPlugin {
    @Getter
    private static Plugin plugin = null;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.getCommand("pvp").setExecutor(new PvpCommand());
        Bukkit.getPluginManager().registerEvents(new OnDamageListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
