package fr.jamailun.quickparty.parties;

import fr.jamailun.quickparty.api.events.PartyDisbandEvent;
import fr.jamailun.quickparty.api.events.PartyInviteEvent;
import fr.jamailun.quickparty.api.events.PartyLeftEvent;
import fr.jamailun.quickparty.api.events.PartyPromoteEvent;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyMember;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PartyImpl implements Party {

    @Getter private final LocalDateTime creationDate = LocalDateTime.now();
    private final SequencedMap<UUID, PartyMemberImpl> members = new LinkedHashMap<>();
    @Getter private PartyMemberImpl leader;

    private final Map<UUID, OfflinePlayer> pendingInvitations = new HashMap<>();

    private final Consumer<UUID> playerQuit;
    private final Consumer<UUID> playerJoined;

    public PartyImpl(@NotNull Player leaderPlayer, Consumer<UUID> playerQuit, BiConsumer<UUID, Party> playerJoined) {
        // Init leader
        leader = new PartyMemberImpl(this, leaderPlayer);
        leader.setPartyLeader(true);
        members.put(leaderPlayer.getUniqueId(), leader);

        // Callbacks
        this.playerQuit = playerQuit;
        this.playerJoined = uuid -> playerJoined.accept(uuid, this);
    }

    @Override
    public @NotNull @UnmodifiableView Collection<PartyMember> getMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    @Override
    public @NotNull @UnmodifiableView Collection<OfflinePlayer> getPendingInvitedPlayers() {
        return Collections.unmodifiableCollection(pendingInvitations.values());
    }

    @Override
    public @Nullable PartyMemberImpl getPartyMember(@NotNull UUID uuid) {
        return members.get(uuid);
    }

    @Override
    public boolean hasMember(@NotNull UUID uuid) {
        return members.containsKey(uuid);
    }

    @Override
    public void invite(@NotNull Player player) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUniqueId());
        pendingInvitations.put(offline.getUniqueId(), offline);

        Bukkit.getPluginManager().callEvent(new PartyInviteEvent(this, player));
    }

    @Override
    public void cancelInvitation(@NotNull UUID uuid) {
        pendingInvitations.remove(uuid);
    }

    @Override
    public void join(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        pendingInvitations.remove(uuid);
        PartyMemberImpl member = new PartyMemberImpl(this, player);
        members.put(uuid, member);

        playerJoined.accept(uuid);
    }

    @Override
    public void leave(@NotNull UUID uuid) {
        basicRemove(uuid, false);

        if(Objects.equals(leader.getUUID(), uuid)) {
            leader = null;
            if(!members.isEmpty()) {
                leader = members.firstEntry().getValue();
            } else {
                // Party is invalid and should be disbanded
                // All invitations are sequentially removed.
                List.copyOf(pendingInvitations.keySet()).forEach(this::cancelInvitation);
            }
        }
    }

    @Override
    public void kick(@NotNull OfflinePlayer player) {
        basicRemove(player.getUniqueId(), true);
    }

    private void basicRemove(UUID uuid, boolean kicked) {
        PartyMember removed = members.remove(uuid);
        if(removed != null) {
            Bukkit.getPluginManager().callEvent(new PartyLeftEvent(this, removed.getOfflinePlayer(), kicked));
        }
        playerQuit.accept(uuid);
    }

    @Override
    public void disband() {
        //TODO disband...


        Bukkit.getPluginManager().callEvent(new PartyDisbandEvent(this));
    }

    @Override
    public void promoteMember(@NotNull OfflinePlayer player) {
        // Remove old
        PartyMemberImpl oldLeader = leader;
        oldLeader.setPartyLeader(false);

        // Set new
        leader = members.get(player.getUniqueId());
        leader.setPartyLeader(true);

        // Events
        Bukkit.getPluginManager().callEvent(new PartyPromoteEvent(this, oldLeader));
    }

    public boolean isValid() {
        return leader != null;
    }
}
