package fr.jamailun.quickparty.api.parties;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a bukkit player, member of a party.
 */
public interface PartyMember {

    /**
     * Check if the member is online.
     * @return true if {@link #getOnlinePlayer()} will return a non-null value.
     */
    boolean isOnline();

    /**
     * Get the offline player instance.
     * @return a non-null instance.
     */
    @NotNull OfflinePlayer getOfflinePlayer();

    /**
     * Get the online player instance.
     * @return {@code null} if the player is not online.
     * @see #isOnline()
     */
    Player getOnlinePlayer();

    /**
     * Get the UUID of the member.
     * @return a non-null UUID. Cannot change for one instance.
     */
    @NotNull UUID getUUID();

    /**
     * Check if this member is the leader.
     * @return true if this member is the party leader. A party can only have one leader.
     */
    boolean isPartyLeader();

    /**
     * Get he party this member belongs to.
     * @return a non-null party instance.
     */
    @NotNull Party getParty();

    /**
     * Send a message to the player, if he's online.
     * @param message the message.
     */
    void sendMessage(@NotNull String message);

    /**
     * Try to refresh the online-player after a reconnect.
     */
    void refreshOnline();

}
