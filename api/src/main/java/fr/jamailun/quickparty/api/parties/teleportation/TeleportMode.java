package fr.jamailun.quickparty.api.parties.teleportation;

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
    MEMBER_TO_MEMBER

}
