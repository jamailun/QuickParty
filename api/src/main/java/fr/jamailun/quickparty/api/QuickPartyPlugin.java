package fr.jamailun.quickparty.api;

import fr.jamailun.quickparty.api.parties.PartiesManager;
import org.jetbrains.annotations.NotNull;

/**
 * Public interface for the QP-Plugin.
 * @see QuickParty
 */
public interface QuickPartyPlugin {

    /**
     * Get the plugin part-manager.
     * @return the non-null instance.
     */
    @NotNull PartiesManager getPartiesManager();

}
