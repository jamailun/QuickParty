package fr.jamailun.quickparty.api;

import com.google.common.base.Preconditions;
import fr.jamailun.quickparty.api.parties.PartiesManager;
import fr.jamailun.quickparty.api.parties.Party;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Entry point to the QuickParty API.
 */
public final class QuickParty {
    private QuickParty() {}

    private static QuickPartyPlugin plugin;

    public static void setPlugin(QuickPartyPlugin plugin) {
        Preconditions.checkState(QuickParty.plugin == null, "Cannot set the QuickParty plugin : has already been set.");
        QuickParty.plugin = plugin;
    }

    public static @NotNull PartiesManager getPartiesManager() {
        return plugin.getPartiesManager();
    }

    public static @Nullable Party getPlayerParty(@NotNull Player player) {
        return getPartiesManager().getPlayerParty(player);
    }
    public static @Nullable Party getPlayerParty(@NotNull UUID uuid) {
        return getPartiesManager().getPlayerParty(uuid);
    }

}
