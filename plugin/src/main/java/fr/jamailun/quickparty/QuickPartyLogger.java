package fr.jamailun.quickparty;

import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.quickparty.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class QuickPartyLogger {

    private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("[HH:mm:ss]");

    private static CommandSender sender;

    private QuickPartyLogger() {}

    private static @NotNull String time() {
        return TIME_FORMAT.format(Instant.now());
    }

    private static @NotNull String prefix(@NotNull String color) {
        return "&b[QPa]" + color + time() + " ";
    }

    static void initialize(@NotNull JavaPlugin plugin) {
        QuickPartyLogger.sender = plugin.getServer().getConsoleSender();
    }

    public static void debug(@NotNull String message) {
        if(QuickPartyConfig.isDebug())
           send(prefix("&3") + "[Debug]&7 " + message);
    }

    public static void info(@NotNull String message) {
        send(prefix("&b") + "[Info]&f " + message);
    }

    public static void warn(@NotNull String message) {
        send(prefix("&6") + "[Warn]&e " + message);
    }

    public static void error(@NotNull String message) {
        send(prefix("&4") + "[Error]&c " + message);
    }

    public static void error(@NotNull String message, @NotNull Throwable t) {
        error(message);
        send("&c" + StringUtils.formatError(t));
    }

    private static void send(@NotNull String message) {
        sender.sendMessage(StringUtils.parseString(message));
    }

}
