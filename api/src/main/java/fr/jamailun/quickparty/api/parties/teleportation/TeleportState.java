package fr.jamailun.quickparty.api.parties.teleportation;

/**
 * State of a {@link TeleportRequest}.
 */
public enum TeleportState {

    /**
     * The invitation is still pending.
     */
    PENDING,

    /**
     * The invitation has been accepted. Player has been teleported.
     */
    ACCEPTED_SUCCESS,

    /**
     * The invitation has been accepted <b>but</b> a problem occurred.
     * For example, the destination player may have become offline, or the requester
     * did not have any money left.
     */
    ACCEPTED_ERROR,

    /**
     * Request refused.
     */
    REFUSED,

    /**
     * Started a new request without finishing this one.
     */
    ABANDONED,

    /**
     * Request expired.
     */
    EXPIRED;

    /**
     * Check if success.
     * @return true if action successful.
     */
    public boolean isSuccess() {
        return this == ACCEPTED_SUCCESS;
    }

}
