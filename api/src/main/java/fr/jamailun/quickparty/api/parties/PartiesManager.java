package fr.jamailun.quickparty.api.parties;

import fr.jamailun.quickparty.api.parties.invitations.PartyInvitation;
import fr.jamailun.quickparty.api.parties.invitations.PartyInvitationResult;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportRequest;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

/**
 * Server {@link Party Parties} manager.
 */
public interface PartiesManager {

    /**
     * Get all existing parties.
     * @return a non-null view of existing parties.
     */
    @NotNull @UnmodifiableView
    Collection<Party> getParties();

    /**
     * Get the {@link Party} of a player.
     * @param player the player instance.
     * @return {@code null} if the player does not belong to any {@link Party}.
     */
    default @Nullable Party getPlayerParty(@NotNull OfflinePlayer player) {
        return getPlayerParty(player.getUniqueId());
    }

    /**
     * Get the {@link Party} of a player.
     * @param playerUuid the player UUID.
     * @return {@code null} if the player does not belong to any {@link Party}.
     */
    @Nullable Party getPlayerParty(@NotNull UUID playerUuid);

    /**
     * Invite a player to the party.
     * @param playerFrom player sending the invitation. If not in a party, a new one will be created.
     * @param playerTo player to receive the invitation.
     * @return a non-null result. Check the success with {@link PartyInvitationResult#isSuccess()}.
     */
    @NotNull PartyInvitationResult invitePlayer(@NotNull Player playerFrom, @NotNull Player playerTo);

    /**
     * Test if a pending {@link PartyInvitation} exists for a {@link Player}.
     * @param player the player to test.
     * @return {@code true} if this player has a pending {@link PartyInvitation}.
     * @see #getInvitationFor(OfflinePlayer)
     */
    boolean hasInvitation(@NotNull OfflinePlayer player);

    /**
     * Test if a pending {@link TeleportRequest} exists for a {@link Player}.
     * @param player the player to test.
     * @return {@code true} if this player has a pending {@link TeleportRequest}.
     * @see #getTeleportRequestFor(OfflinePlayer)
     */
    default boolean hasTeleportRequest(@NotNull OfflinePlayer player) {
        return getTeleportRequestFor(player) != null;
    }

    /**
     * Get the {@link PartyInvitation} of a {@link Player}.
     * @param player the player to test.
     * @return {@code null} if this player has no pending {@link PartyInvitation}.
     */
    @Nullable PartyInvitation getInvitationFor(@NotNull OfflinePlayer player);

    /**
     * Get the {@link TeleportRequest} of a {@link Player}.
     * @param player the player to test.
     * @return {@code null} if this player has no pending {@link TeleportRequest}.
     */
    @Nullable TeleportRequest getTeleportRequestFor(@NotNull OfflinePlayer player);

}
