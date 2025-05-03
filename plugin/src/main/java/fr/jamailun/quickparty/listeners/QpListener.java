package fr.jamailun.quickparty.listeners;

import fr.jamailun.quickparty.QuickPartyLogger;
import fr.jamailun.quickparty.QuickPartyMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

abstract class QpListener implements Listener {

    public QpListener(@NotNull QuickPartyMain plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        QuickPartyLogger.debug("Registering events on " + getClass().getSimpleName());
    }

    protected void sendMessage(@NotNull Player player, @NotNull String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
