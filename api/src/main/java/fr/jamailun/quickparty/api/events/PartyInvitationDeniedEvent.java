package fr.jamailun.quickparty.api.events;

import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.invitations.PartyInvitation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event sent when a {@link PartyInvitation} has been denied by a player.
 */
@RequiredArgsConstructor
@Getter
public class PartyInvitationDeniedEvent extends PartyEvent {

    private final @NotNull PartyInvitation invitation;
    private final @NotNull Player player;

    @Override
    public @NotNull Party getParty() {
        return invitation.getParty();
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
