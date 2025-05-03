package fr.jamailun.quickparty.api.events;

import fr.jamailun.quickparty.api.parties.Party;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
@Getter
public class PartyLeftEvent extends Event {

    private final Party party;
    private final OfflinePlayer player;
    private final boolean kicked;

    // -- boilerplate

    private static final HandlerList handlers = new HandlerList();
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
