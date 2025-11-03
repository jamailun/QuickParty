package fr.jamailun.quickparty.api.parties.teleportation;

import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyMember;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * A teleportation request between two players.
 */
public interface TeleportRequest {

    /**
     * Get the {@link Party} the invitation is for.
     * @return the non-null Party reference.
     */
    @NotNull Party getParty();

    /**
     * Get the state of the teleportation request.
     * @return a non-null enum state entry.
     */
    @NotNull TeleportState getState();

    /**
     * Get the mode of the teleportation.
     * @return a non-null enum value.
     */
    @NotNull TeleportMode getMode();

    /**
     * Instant the request was made.
     * @return a non-null date.
     */
    @NotNull LocalDateTime getDate();

    /**
     * The expiration date.
     * @return a non-null date.
     */
    @NotNull LocalDateTime getExpirationDate();

    /**
     * Player to teleport to.
     * @return the player. If offline, the request will be cancelled.
     */
    @NotNull PartyMember getDestination();

    /**
     * Player to teleport. <b>Not always the requester ! ({@link TeleportMode#ALL_TO_LEADER})</b>
     * @return the player to be teleported. If offline, the request will be cancelled.
     */
    @NotNull PartyMember getPlayer();

    /**
     * Cancel the request.
     */
    void cancel();

    /**
     * Accept the request.
     */
    void accept();

}
