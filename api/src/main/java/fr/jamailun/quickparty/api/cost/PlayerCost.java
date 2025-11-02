package fr.jamailun.quickparty.api.cost;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A cost.
 */
public interface PlayerCost {

    /**
     * Test if a player can pay the cost.
     * @param player player to pay.
     * @return true if the player can pay.
     */
    boolean canPay(@NotNull Player player);

    /**
     * Make the player pay.
     */
    void pay(@NotNull Player player);

}
