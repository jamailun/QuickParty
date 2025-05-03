package fr.jamailun.quickparty.parties;

import fr.jamailun.quickparty.api.events.PartyJoinEvent;
import fr.jamailun.quickparty.api.parties.*;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class PartiesManagerImpl implements PartiesManager {

    private final Set<Party> parties = new HashSet<>();
    // players -> party : Reverse access for speed !
    private final Map<UUID, Party> partiesAsMap = new HashMap<>();

    // players -> party : invitation
    final Map<UUID, PartyInvitation> invitations = new HashMap<>();

    @Override
    public @NotNull @UnmodifiableView Collection<Party> getParties() {
        return Collections.unmodifiableCollection(parties);
    }

    @Override
    public @Nullable Party getPlayerParty(@NotNull UUID playerUuid) {
        return partiesAsMap.get(playerUuid);
    }

    @Override
    public @NotNull PartyInvitationResult invitePlayer(@NotNull Player playerFrom, @NotNull Player playerTo) {
        Party party = getPlayerParty(playerFrom);
        if(party != null) {
            if(party.getSize() >= QuickPartyConfig.getInstance().getMaxPartySize()) {
                return PartyInvitationState.PARTY_FULL.asError();
            }
            party.invite(playerTo);
            return PartyInvitationState.INVITATION_SUCCESS.asSuccess(party);
        }

        Party newParty = new PartyImpl(playerFrom, this);
        parties.add(newParty);
        partiesAsMap.put(playerFrom.getUniqueId(), newParty);

        // Event
        Bukkit.getPluginManager().callEvent(new PartyJoinEvent(newParty, playerFrom, true));
        newParty.invite(playerTo);

        return PartyInvitationState.PARTY_CREATED.asSuccess(newParty);
    }

    @Override
    public boolean hasInvitation(@NotNull OfflinePlayer player) {
        return invitations.containsKey(player.getUniqueId());
    }

    @Override
    public PartyInvitation getInvitationFor(@NotNull OfflinePlayer player) {
        return invitations.get(player.getUniqueId());
    }

    void playerQuit(@NotNull UUID uuid) {
        partiesAsMap.remove(uuid);
    }

    void playerJoined(@NotNull UUID uuid, @NotNull Party party) {
        partiesAsMap.put(uuid, party);
    }
}
