package fr.jamailun.quickparty.api;

import fr.jamailun.quickparty.api.parties.PartiesManager;
import org.jetbrains.annotations.NotNull;

public interface QuickPartyPlugin {

    @NotNull PartiesManager getPartiesManager();

}
