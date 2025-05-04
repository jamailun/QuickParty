package fr.jamailun.quickparty.commands;

import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PartyAdminCommand extends CommandHelper implements CommandExecutor, TabCompleter {

    private static final List<String> ARGS = List.of("reload");

    public PartyAdminCommand() {
        PluginCommand cmd = Bukkit.getPluginCommand("party-admin");
        assert cmd != null;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull[] args) {
        if("reload".equalsIgnoreCase(args[0])) {
            QuickPartyConfig.getInstance().reload();
            return success(sender, "Configuration reloaded.");
        }
        return error(sender, "Unexpected argument. Expected one of " + ARGS);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull[] args) {
        if(args.length == 1) {
            String arg0 = args[0].toLowerCase();
            return ARGS.stream()
                    .filter(s -> s.contains(arg0))
                    .toList();
        }
        return List.of();
    }
}
