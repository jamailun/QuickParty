package fr.jamailun.quickparty.api;

import com.google.common.base.Preconditions;
import fr.jamailun.quickparty.api.cost.PlayerCostsRegistry;
import fr.jamailun.quickparty.api.parties.PartiesManager;
import fr.jamailun.quickparty.api.parties.Party;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Entry point to the QuickParty API.
 */
public final class QuickParty {
    private QuickParty() {}

    private static QuickPartyPlugin plugin;

    @ApiStatus.Internal
    public static void setPlugin(QuickPartyPlugin plugin) {
        Preconditions.checkState(QuickParty.plugin == null, "Cannot set the QuickParty plugin : has already been set.");
        QuickParty.plugin = plugin;
    }

    /**
     * Get the plugin party-manager.
     * @return the non-null instance.
     */
    public static @NotNull PartiesManager getPartiesManager() {
        return plugin.getPartiesManager();
    }

    /**
     * Quick access for a player party.
     * @param player player instance to get the {@link Party} of.
     * @return {@code null} if no {@link Party} found.
     */
    public static @Nullable Party getPlayerParty(@NotNull Player player) {
        return getPartiesManager().getPlayerParty(player);
    }

    /**
     * Quick access for a player party.
     * @param uuid player's UUID to get the {@link Party} of.
     * @return {@code null} if no {@link Party} found.
     */
    public static @Nullable Party getPlayerParty(@NotNull UUID uuid) {
        return getPartiesManager().getPlayerParty(uuid);
    }

    /**
     * Get the player costs registry.
     * @return the non-null instance.
     */
    public static @NotNull PlayerCostsRegistry getCostsRegistry() {
        return plugin.getCostsRegistry();
    }

}
