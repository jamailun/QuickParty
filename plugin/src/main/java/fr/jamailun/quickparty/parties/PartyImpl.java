package fr.jamailun.quickparty.parties;

import com.google.common.base.Preconditions;
import fr.jamailun.quickparty.QuickPartyScheduler;
import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.events.PartyDisbandEvent;
import fr.jamailun.quickparty.api.events.PartyInviteEvent;
import fr.jamailun.quickparty.api.events.PartyLeftEvent;
import fr.jamailun.quickparty.api.events.PartyLeftEvent.LeaveReason;
import fr.jamailun.quickparty.api.events.PartyPromoteEvent;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.invitations.PartyInvitation;
import fr.jamailun.quickparty.api.parties.PartyMember;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportRequest;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.quickparty.parties.teleportation.TeleportRequestImpl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.time.LocalDateTime;
import java.util.*;

public class PartyImpl implements Party {

    @Getter private final LocalDateTime creationDate = LocalDateTime.now();
    private final SequencedMap<UUID, PartyMemberImpl> members = new LinkedHashMap<>();
    @Getter private PartyMemberImpl leader;

    private final Map<UUID, PartyInvitation> pendingInvitations = new HashMap<>();
    private final Map<UUID, TeleportRequestImpl> pendingTpRequests = new HashMap<>();

    private final PartiesManagerImpl manager;

    public PartyImpl(@NotNull Player leaderPlayer, @NotNull PartiesManagerImpl manager) {
        // Init leader
        leader = new PartyMemberImpl(this, leaderPlayer);
        leader.setPartyLeader(true);
        members.put(leaderPlayer.getUniqueId(), leader);

        // Callbacks
        this.manager = manager;
    }

    @Override
    public @NotNull @UnmodifiableView Collection<PartyMember> getMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    @Override
    public @NotNull @UnmodifiableView Collection<PartyInvitation> getPendingInvitations() {
        return Collections.unmodifiableCollection(pendingInvitations.values());
    }

    @Override
    public @NotNull @UnmodifiableView Collection<TeleportRequest> getPendingTeleportRequests() {
        return Collections.unmodifiableCollection(pendingTpRequests.values());
    }

    @Override
    public @Nullable PartyMemberImpl getPartyMember(@NotNull UUID uuid) {
        return members.get(uuid);
    }

    @Override
    public @Nullable PartyMemberImpl getPartyMember(@NotNull OfflinePlayer player) {
        return members.get(player.getUniqueId());
    }

    @Override
    public boolean hasMember(@NotNull UUID uuid) {
        return members.containsKey(uuid);
    }

    @Override
    public void invite(@NotNull Player player) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUniqueId());
        PartyInvitation invitation = new PartyInvitationImpl(this, player);
        pendingInvitations.put(offline.getUniqueId(), invitation);

        manager.invitations.put(player.getUniqueId(), invitation);
        Bukkit.getPluginManager().callEvent(new PartyInviteEvent(this, player));
    }

    @Override
    public void newTeleportRequest(@NotNull Player player, @NotNull Player destination, @NotNull TeleportMode mode) {
        PartyMember member = getPartyMember(player);
        PartyMember target = getPartyMember(destination);
        Preconditions.checkArgument(member != null, "Player " + player + " is not in the party " + this + " !");
        Preconditions.checkArgument(target != null, "Destination " + target + " is not in the party " + this + " !");

        // Cancel old request :
        TeleportRequestImpl previous = pendingTpRequests.remove(player.getUniqueId());
        if(previous != null && previous.isPending()) {
            previous.abandon();
        }

        // New request
        TeleportRequestImpl request = new TeleportRequestImpl(member, target, mode);
        pendingTpRequests.put(player.getUniqueId(), request);

        // Change state later
        QuickPartyScheduler.runLaterAsync(request::expired, QuickPartyConfig.getInstance().getTeleportRequestExpiration());

        // Message and sound
        QuickPartyConfig.getInstance().sendTpRequest(destination, player, mode);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1f, 1.05f);
    }

    @Override
    public @Nullable TeleportRequest getTeleportRequestOf(@NotNull OfflinePlayer player) {
        return pendingTpRequests.get(player.getUniqueId());
    }

    @Override
    public void cancelInvitation(@NotNull UUID uuid) {
        pendingInvitations.remove(uuid);
        manager.invitations.remove(uuid);
    }

    @Override
    public void join(@NotNull Player player) {
        // Leave previous ?
        Party oldParty = QuickParty.getPlayerParty(player);
        if(oldParty != null)
            oldParty.leave(player.getUniqueId());

        // Join new
        String message = QuickPartyConfig.getI18n("players.invitation.join-alert")
                .replace("%player", player.getName());
        getMembers().forEach(m -> m.sendMessage(message));

        UUID uuid = player.getUniqueId();
        pendingInvitations.remove(uuid);
        manager.invitations.remove(uuid);
        PartyMemberImpl member = new PartyMemberImpl(this, player);
        members.put(uuid, member);

        manager.playerJoined(uuid, this);
    }

    @Override
    public void leave(@NotNull UUID uuid) {
        basicRemove(uuid, LeaveReason.NORMAL);

        if(Objects.equals(leader.getUUID(), uuid)) {
            leader = null;
            if(!members.isEmpty()) {
                leader = members.firstEntry().getValue();
                leader.setPartyLeader(true);
            } else {
                // Party is invalid and should be disbanded
                // All invitations are sequentially removed.
                List.copyOf(pendingInvitations.keySet()).forEach(this::cancelInvitation);
                disband();
            }
        }
    }

    @Override
    public void kick(@NotNull OfflinePlayer player) {
        basicRemove(player.getUniqueId(), LeaveReason.KICKED);
        sendMessage(i18n("kick-success").replace("%player", Objects.requireNonNullElse(player.getName(), player.toString())));
    }

    private void basicRemove(UUID uuid, LeaveReason reason) {
        PartyMember removed = members.remove(uuid);
        if(removed != null) {
            Bukkit.getPluginManager().callEvent(new PartyLeftEvent(this, removed.getOfflinePlayer(), reason));
            switch (reason) {
                case NORMAL -> removed.sendMessage(i18n( "leave"));
                case KICKED -> removed.sendMessage(i18n( "kick-info"));
                case DISBANDED -> removed.sendMessage(i18n( "disbanded"));
            }
        }
        manager.playerQuit(uuid);
    }

    @Override
    public void disband() {
        // Event before the removal of members & invitations
        Bukkit.getPluginManager().callEvent(new PartyDisbandEvent(this));

        // Remove all invitations and members
        List.copyOf(pendingInvitations.keySet()).forEach(this::cancelInvitation);
        List.copyOf(members.values()).forEach(p -> basicRemove(p.getUUID(), LeaveReason.DISBANDED));

        manager.removeParty(this);
    }

    @Override
    public void promoteMember(@NotNull OfflinePlayer player) {
        PartyMemberImpl member = members.get(player.getUniqueId());
        Preconditions.checkArgument(member != null, "Provided player " + player.getUniqueId() + " does not belong to the party.");

        // Remove old
        PartyMemberImpl oldLeader = leader;
        oldLeader.setPartyLeader(false);

        // Set new
        leader = member;
        leader.setPartyLeader(true);

        // Events
        Bukkit.getPluginManager().callEvent(new PartyPromoteEvent(this, oldLeader));
        sendMessage(i18n("promote-success").replace("%leader", member.getName()).replace("%old_leader", oldLeader.getName()));
    }

    private void sendMessage(@NotNull String message) {
        getMembers().forEach(m -> m.sendMessage(message));
    }

    private static @NotNull String i18n(@NotNull String key) {
        return QuickPartyConfig.getI18n("players." + key);
    }
}
