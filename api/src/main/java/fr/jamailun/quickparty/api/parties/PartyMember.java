package fr.jamailun.quickparty.api.parties;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PartyMember {

    boolean isOnline();

    @NotNull OfflinePlayer getOfflinePlayer();

    Player getOnlinePlayer();

    @NotNull UUID getUUID();

    boolean isPartyLeader();

    @NotNull Party getParty();

    void sendMessage(@NotNull String message);

}
