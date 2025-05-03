package fr.jamailun.quickparty.api.parties;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum PartyInvitationState {

    /**
     * Error, cannot invite this player because he is already in the party.
     */
    PLAYER_ALREADY_IN_PARTY(false),

    /**
     * Error, cannot invite this player because the party is full.
     */
    PARTY_FULL(false),

    /**
     * Success. the party already exists, and the player has been invited.
     */
    INVITATION_SUCCESS(true),

    /**
     * Success. the party already exists, and the player has been invited.
     */
    PARTY_CREATED(true);

    private final boolean success;
    PartyInvitationState(boolean success) {
        this.success = success;
    }

    public @NotNull PartyInvitationResult asSuccess(@NotNull Party party) {
        Preconditions.checkState(success, "State " + this + " cannot return a successful result.");
        return new PartyInvitationResult(this, party);
    }

    public @NotNull PartyInvitationResult asError() {
        Preconditions.checkState(!success, "State " + this + " cannot return an error result.");
        return new PartyInvitationResult(this, null);
    }

}
