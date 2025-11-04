package fr.jamailun.quickparty.api.parties.teleportation;

import fr.jamailun.quickparty.api.parties.PartyMember;
import org.jetbrains.annotations.NotNull;

/**
 * Modes of teleport.
 */
public enum TeleportMode {

    /**
     * Leader makes to call to TP everyone to him.
     */
    ALL_TO_LEADER,

    /**
     * Leader can make a request to teleport to any member.
     */
    LEADER_TO_MEMBER,

    /**
     * Anyone can make a request to TP to the leader.
     */
    MEMBER_TO_LEADER,

    /**
     * Anyone can make a request to TP to anyone-else.
     */
    MEMBER_TO_MEMBER;

    /**
     * Compute the teleport mode between two players.
     * @param player member the teleport request will be from.
     * @param target destination member.
     * @return a non-null value, but never {@link #ALL_TO_LEADER}.
     */
    public static @NotNull TeleportMode evaluateWith(@NotNull PartyMember player, @NotNull PartyMember target) {
        if(player.isPartyLeader())
            return LEADER_TO_MEMBER;
        if(target.isPartyLeader())
            return MEMBER_TO_LEADER;
        return MEMBER_TO_MEMBER;
    }

}
