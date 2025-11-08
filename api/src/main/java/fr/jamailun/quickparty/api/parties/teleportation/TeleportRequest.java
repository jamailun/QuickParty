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
     * Get the name of the player destination.
     * @return the destination username
     */
    default @NotNull String getInvitationName() {
        return getDestination().getName();
    }

    /**
     * Player that need to accept the invitation.
     * @return a non-null member.
     */
    @NotNull PartyMember getPlayerToAccept();

    /**
     * Player that waits for the other one for the teleportation.
     * @return a non-null member.
     */
    @NotNull PartyMember getPlayerWaiting();

    /**
     * Player to teleport to. Varies according to the value of {@link #getMode()}.
     * @return a non-null member.
     */
    default @NotNull PartyMember getDestination() {
        return getMode() == TeleportMode.ALL_TO_LEADER ? getPlayerWaiting() : getPlayerToAccept();
    }

    /**
     * Player to be teleported. Varies according to the value of {@link #getMode()}.
     * @return a non-null member.
     */
    default @NotNull PartyMember getPlayerToTeleport() {
        return getMode() == TeleportMode.ALL_TO_LEADER ? getPlayerToAccept() : getPlayerWaiting();
    }

    /**
     * Cancel the request.
     */
    void cancel();

    /**
     * Accept the request.
     */
    void accept();

}
