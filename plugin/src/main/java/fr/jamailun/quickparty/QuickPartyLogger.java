package fr.jamailun.quickparty;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class QuickPartyLogger {

    private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("[HH:mm:ss]");

    private static String prefix;
    private static CommandSender sender;

    private QuickPartyLogger() {}

    private static @NotNull String time() {
        return TIME_FORMAT.format(Instant.now());
    }

    private static @NotNull String prefix(@NotNull String color) {
        return  "&b[QPa] " + color + time() + " ";
    }

    static void initialize(@NotNull JavaPlugin plugin) {
        QuickPartyLogger.sender = plugin.getServer().getConsoleSender();
    }

    public static void debug(@NotNull String message) {
        send(prefix("&3") + "[Debug]" + prefix + "&7 " + message);
    }

    public static void info(@NotNull String message) {
        send(prefix("&b") + "[Info]" + prefix + "&f " + message);
    }

    public static void warn(@NotNull String message) {
        send(prefix("&6") + "[Warn]" + prefix + "&e " + message);
    }

    public static void warn(@NotNull String message, @NotNull Throwable t) {
        warn(message);
        send("&e" + formatError(t));
    }

    public static void error(@NotNull String message) {
        send(prefix("&4") + "[Error]" + prefix + "&c " + message);
    }

    public static void error(@NotNull String message, @NotNull Throwable t) {
        error(message);
        send("&c" + formatError(t));
    }

    @SuppressWarnings("deprecation")
    private static void send(@NotNull String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private static @NotNull String formatError(@NotNull Throwable t) {
        StringBuilder sb = new StringBuilder();
        t.printStackTrace(new PrintWriter(new Writer() {
            @Override
            public void write(char @NotNull [] buf, int off, int len) {
                sb.append(String.copyValueOf(buf, off, len));
            }
            @Override
            public void write(@NotNull String str) {
                sb.append(str);
            }
            @Override public void flush() {}
            @Override public void close() {}
        }));
        return sb.toString();
    }

}
