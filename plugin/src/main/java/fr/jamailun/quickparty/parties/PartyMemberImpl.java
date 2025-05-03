package fr.jamailun.quickparty.parties;

import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyMember;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.*;

@Getter @Setter
public class PartyMemberImpl implements PartyMember {

    private final Party party;
    private final OfflinePlayer offlinePlayer;
    private boolean isPartyLeader;

    PartyMemberImpl(@NotNull Party party, @NotNull OfflinePlayer player) {
        this.party = party;
        this.offlinePlayer = player;
    }

    @Override
    public boolean isOnline() {
        return offlinePlayer.isOnline();
    }

    @Override
    public Player getOnlinePlayer() {
        return offlinePlayer.getPlayer();
    }

    @Override
    public @NotNull UUID getUUID() {
        return offlinePlayer.getUniqueId();
    }

}
