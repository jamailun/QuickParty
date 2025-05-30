package fr.jamailun.quickparty.commands;

import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.quickparty.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * Helper methods for command manipulation.
 */
public abstract class CommandHelper {

    @SafeVarargs
    protected static <T> @NotNull @UnmodifiableView List<T> listOf(@NotNull List<T> list, @NotNull T @NotNull ... elements) {
        List<T> copy = new ArrayList<>(list);
        copy.addAll(List.of(elements));
        return Collections.unmodifiableList(copy);
    }

    protected boolean error(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(StringUtils.parseString("&4[&cERROR&4]&c " + message.replace("&r", "&c")));
        return true;
    }

    protected boolean info(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(StringUtils.parseString("&3[&fINFO&3]&7 " + message.replace("&r", "&7")));
        return true;
    }

    protected boolean success(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(StringUtils.parseString("&2[&aSUCCESS&2]&f " + message.replace("&r", "&f")));
        return true;
    }

    protected String i18n(String key) {
        return QuickPartyConfig.getI18n("players." + key);
    }
}
