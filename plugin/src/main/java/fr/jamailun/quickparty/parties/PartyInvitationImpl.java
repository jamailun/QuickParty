package fr.jamailun.quickparty.parties;

import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyInvitation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PartyInvitationImpl implements PartyInvitation {

    private final @NotNull LocalDateTime date = LocalDateTime.now();
    private final @NotNull Party party;
    private final @NotNull OfflinePlayer invitedPlayer;

}
