package fr.jamailun.quickparty.api.events;

import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyMember;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when the leader of a party changed.
 */
@RequiredArgsConstructor
@Getter
public class PartyPromoteEvent extends PartyEvent {

    private final Party party;
    private final PartyMember oldLeader;

    /**
     * Get the new leader of the party.
     * @return the new leader.
     */
    public @NotNull PartyMember getNewLeader() {
        return party.getLeader();
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
