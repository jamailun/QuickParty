package fr.jamailun.quickparty;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Internal scheduler.
 */
public final class QuickPartyScheduler {

    private static JavaPlugin plugin;

    static void initialize(@NotNull JavaPlugin plugin) {
        QuickPartyScheduler.plugin = plugin;
    }

    /**
     * Run task later, async.
     * @param runnable runnable.
     * @param duration duration.
     */
    public static void runLater(@NotNull Runnable runnable, @NotNull Duration duration) {
        long millis = duration.toMillis();
        long ticks = millis / 50;
        Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
    }

    /**
     * Run task later, async.
     * @param runnable runnable.
     * @param duration duration.
     */
    public static void runLaterAsync(@NotNull Runnable runnable, @NotNull Duration duration) {
        long millis = duration.toMillis();
        long ticks = millis / 50;
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks);
    }

}
