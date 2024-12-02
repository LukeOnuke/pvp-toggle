package com.lukeonuke.pvptoggle.service;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderExpansionService extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "pvp-toggle";
    }

    @Override
    public @NotNull String getAuthor() {
        return "lukeonuke & contributors";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(params.equals("_vulnerable")){
            return ChatFormatterService.booleanHumanReadable(PvpService.isPvpEnabled(player));
        }
        // TODO: PVP State placeholder.
        return null;
    }
}
