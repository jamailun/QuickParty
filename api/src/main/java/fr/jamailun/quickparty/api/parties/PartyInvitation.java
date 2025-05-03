package fr.jamailun.quickparty.api.parties;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public interface PartyInvitation {

    @NotNull LocalDateTime getDate();

    @NotNull Party getParty();

    @NotNull OfflinePlayer getInvitedPlayer();

}
