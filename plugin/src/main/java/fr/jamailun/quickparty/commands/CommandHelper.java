package fr.jamailun.quickparty.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * Helper methods for command manipulation.
 */
public abstract class CommandHelper {

    protected static <T> @NotNull @UnmodifiableView List<T> listOf(@UnmodifiableView @NotNull List<T> list, @NotNull T @NotNull ... elements) {
        List<T> copy = new ArrayList<>(list);
        copy.addAll(List.of(elements));
        return Collections.unmodifiableList(copy);
    }

    protected boolean unexpectedArgument(@NotNull CommandSender sender, @NotNull String arg, @NotNull List<String> allowed) {
        return error(sender, "Invalid arg '" + arg + "'. Expected : " + Arrays.toString(allowed.toArray()) + ".");
    }

    protected boolean missingArgument(@NotNull CommandSender sender, @NotNull String missing) {
        return error(sender, "Argument missing. Expected : " + missing + ".");
    }
    protected boolean missingArgument(@NotNull CommandSender sender, @NotNull List<String> allowed) {
        return error(sender, "Argument missing. Expected : " + Arrays.toString(allowed.toArray()) + ".");
    }

    protected boolean error(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4[&cERROR&4]&c " + message.replace("&r", "&c")));
        return true;
    }

    protected boolean info(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3[&fINFO&3]&7 " + message.replace("&r", "&7")));
        return true;
    }

    protected boolean success(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&2[&aSUCCESS&2]&f " + message.replace("&r", "&f")));
        return true;
    }

    protected @NotNull String[] next(@NotNull String @NotNull [] source) {
        return next(source, 1);
    }

    protected @NotNull String[] next(@NotNull String @NotNull [] source, int size) {
        if(source.length == 0) return new String[0];
        String[] target = new String[source.length - size];
        System.arraycopy(source, size, target, 0, target.length);
        return target;
    }

    protected @Nullable Integer readInt(@NotNull CommandSender sender, @NotNull String value) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException ignored) {
            error(sender, "Invalid integer value: '"+value+"'");
            return null;
        }
    }
    protected @Nullable Double readDouble(@NotNull CommandSender sender, @NotNull String value) {
        try {
            return Double.parseDouble(value);
        } catch(NumberFormatException ignored) {
            error(sender, "Invalid integer value: '"+value+"'");
            return null;
        }
    }

    protected @NotNull String absorbRemaining(int offset, @NotNull String@NotNull [] args) {
        StringJoiner sj = new StringJoiner(" ");
        for(int i = offset; i < args.length; i++)
            sj.add(args[i]);
        return sj.toString();
    }

    protected List<String> smartAbsorb(@NotNull String@NotNull [] args, int offset) {
        List<String> parts = new ArrayList<>();
        boolean block = false;
        StringJoiner joiner = new StringJoiner(" ");
        for(int i = offset; i < args.length; i++) {
            String current = args[i];
            if(block) {
                if(current.endsWith("\"")) {
                    block = false;
                    String esc = current.substring(0, current.length() - 1);
                    joiner.add(esc);
                    parts.add(joiner.toString());
                    joiner = new StringJoiner(" ");
                } else {
                    joiner.add(current);
                }
            } else if(current.startsWith("\"")) {
                block = true;
                joiner.add(current.substring(1));
            } else {
                parts.add(current.replace("\\\"", "\""));
            }
        }
        if(block) {
            parts.add(joiner.toString());
        }
        return parts;
    }

}
