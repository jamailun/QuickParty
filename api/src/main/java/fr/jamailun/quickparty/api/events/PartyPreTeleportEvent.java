package fr.jamailun.quickparty.api.events;

import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class PartyPreTeleportEvent extends PartyEvent implements Cancellable {

    @Setter
    private boolean cancelled = false;

    private final @NotNull Party party;

    /**
     * Player that will be teleported.
     */
    private final @NotNull Player player;

    /**
     * Player that will be teleported.
     */
    private final @NotNull Player destination;

    /**
     * Mode of teleportation.
     */
    private final @NotNull TeleportMode mode;


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
