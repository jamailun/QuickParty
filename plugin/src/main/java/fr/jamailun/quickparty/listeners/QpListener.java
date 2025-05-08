package fr.jamailun.quickparty.listeners;

import fr.jamailun.quickparty.QuickPartyLogger;
import fr.jamailun.quickparty.QuickPartyMain;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

abstract class QpListener implements Listener {

    public QpListener(@NotNull QuickPartyMain plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        QuickPartyLogger.debug("Registering events on " + getClass().getSimpleName());
    }

}
