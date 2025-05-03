package fr.jamailun.quickparty.api.parties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PartyInvitationResult(
        @NotNull PartyInvitationState state,
        @Nullable Party party
) {

    boolean isSuccess() {
        return state.isSuccess();
    }

}
