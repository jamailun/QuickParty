package fr.jamailun.quickparty.api.parties;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public interface Party {

    @NotNull @UnmodifiableView
    Collection<PartyMember> getMembers();

    @NotNull @UnmodifiableView
    Collection<OfflinePlayer> getPendingInvitedPlayers();

    default int getSize() {
        return getMembers().size() + getPendingInvitedPlayers().size();
    }

    @Nullable PartyMember getPartyMember(@NotNull UUID uuid);

    @NotNull PartyMember getLeader();

    @NotNull LocalDateTime getCreationDate();

    boolean hasMember(@NotNull UUID uuid);

    void invite(@NotNull Player player);

    void cancelInvitation(@NotNull UUID uuid);

    void join(@NotNull Player player);

    void leave(@NotNull UUID playerUuid);

    void kick(@NotNull OfflinePlayer player);

    void disband();

    void promoteMember(@NotNull OfflinePlayer player);

}
