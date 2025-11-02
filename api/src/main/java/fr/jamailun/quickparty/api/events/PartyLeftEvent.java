package fr.jamailun.quickparty.api.events;

import fr.jamailun.quickparty.api.parties.Party;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event sent when a player left a party.
 */
@RequiredArgsConstructor
@Getter
public class PartyLeftEvent extends PartyEvent {

    private final Party party;
    private final OfflinePlayer player;
    private final LeaveReason reason;

    /**
     * Reasons of a {@link PartyLeftEvent}.
     */
    public enum LeaveReason {
        /**
         * The player left on his own accord.
         */
        NORMAL,

        /**
         * The player has been kicked by the leader.
         */
        KICKED,

        /**
         * The party has been disbanded. As such, all players have been kicked.
         */
        DISBANDED
    }

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
