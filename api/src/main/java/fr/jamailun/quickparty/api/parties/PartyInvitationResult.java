package fr.jamailun.quickparty.api.parties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Result of a {@link PartyInvitation}.
 * @param state state of the result.
 * @param party party the invitation is from.
 */
public record PartyInvitationResult(
        @NotNull PartyInvitationState state,
        @Nullable Party party
) {

    /**
     * Test if it's a success.
     * @return {@code true} if invitation has been accepted and player has joined.
     */
    public boolean isSuccess() {
        return state.isSuccess();
    }

}
