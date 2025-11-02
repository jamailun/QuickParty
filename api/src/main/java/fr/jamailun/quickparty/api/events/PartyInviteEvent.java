package fr.jamailun.quickparty.api.events;

import fr.jamailun.quickparty.api.parties.Party;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event sent when a {@link fr.jamailun.quickparty.api.parties.PartyInvitation} has been sent to another player.
 */
@RequiredArgsConstructor
@Getter
public class PartyInviteEvent extends PartyEvent {

    private final Party party;
    private final Player player;

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
