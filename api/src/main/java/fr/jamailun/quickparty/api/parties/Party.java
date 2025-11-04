package fr.jamailun.quickparty.api.parties;

import fr.jamailun.quickparty.api.parties.invitations.PartyInvitation;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportRequest;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * A party is a group of {@link Player players}, with one leader. <br/>
 * Each player is represented as a {@link PartyMember}.
 */
public interface Party {

    /**
     * Get all party-players.
     * @return a non-null view of {@link PartyMember PartyMembers}.
     */
    @NotNull @UnmodifiableView
    Collection<PartyMember> getMembers();

    /**
     * Get all pending invitations.
     * @return a non-null view of {@link PartyInvitation PartyInvitations}.
     */
    @NotNull @UnmodifiableView
    Collection<PartyInvitation> getPendingInvitations();

    /**
     * Get all pending teleport requests.
     * @return a non-null view of {@link TeleportRequest TeleportRequests}.
     */
    @NotNull @UnmodifiableView
    Collection<TeleportRequest> getPendingTeleportRequests();

    /**
     * Get the <b>total</b> pending size.
     * @return the sum of the players and invitations counts.
     */
    default int getSize() {
        return getMembers().size() + getPendingInvitations().size();
    }

    /**
     * Get a party-member.
     * @param uuid the UUID of the player to get.
     * @return {@code null} if the UUID does not match any member.
     */
    @Nullable PartyMember getPartyMember(@NotNull UUID uuid);

    /**
     * Get a party-member
     * @param player player to get.
     * @return {@code null} if the player is not in the party.
     */
    @Nullable PartyMember getPartyMember(@NotNull OfflinePlayer player);

    /**
     * Get the party leader.
     * @return the leader member instance.
     */
    @NotNull PartyMember getLeader();

    /**
     * Get the date of the party creation.
     * @return a non-null date.
     */
    @NotNull LocalDateTime getCreationDate();

    /**
     * Test if a player belongs to the party.
     * @param uuid the UUID of the player to test.
     * @return {@code true} if the player belongs to the party.
     */
    boolean hasMember(@NotNull UUID uuid);

    /**
     * Invite a new player to the party.
     * @param player player to invite.
     */
    void invite(@NotNull Player player);

    /**
     * Make a new teleportation request.
     * @param player player to teleport.
     * @param destination player to teleport the other one.
     * @param mode mode of teleportation.
     */
    void newTeleportRequest(@NotNull Player player, @NotNull Player destination, @NotNull TeleportMode mode);

    /**
     * Get a player pending {@link TeleportRequest}.
     * @param player player to get the request of.
     * @return {@code null} if no request is pending for this player.
     */
    @Nullable TeleportRequest getTeleportRequestOf(@NotNull OfflinePlayer player);

    /**
     * Cancel a pending invitation.
     * @param uuid the UUID of the player to cancel the invitation of.
     */
    void cancelInvitation(@NotNull UUID uuid);

    /**
     * Make a player joins the party.
     * @param player player to join.
     */
    void join(@NotNull Player player);

    /**
     * Make a player leave the party.
     * @param playerUuid UUID of the player to leave.
     */
    void leave(@NotNull UUID playerUuid);

    /**
     * Kick a player from the party.
     * @param player player to kick.
     */
    void kick(@NotNull OfflinePlayer player);

    /**
     * Disband the party. Cannot be undone.
     */
    void disband();

    /**
     * Promote a member as the new party leader.
     * @param player player to promote. Must already be in the party.
     * @throws IllegalArgumentException if the provided player does not belong to this party.
     */
    void promoteMember(@NotNull OfflinePlayer player);

}
