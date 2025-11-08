package fr.jamailun.quickparty.costs;

import fr.jamailun.quickparty.QuickPartyLogger;
import fr.jamailun.quickparty.api.cost.PlayerCost;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Cost to pay with real {@link ItemStack}.
 */
public record PlayerCostItem(
    String type,
    int count
) implements PlayerCost {

    @Override
    public boolean canPay(@NotNull Player player) {
        ItemStack cost = getItemStack();
        if(cost == null) return true;
        return player.getInventory().containsAtLeast(cost, cost.getAmount());
    }

    @Override
    public void pay(@NotNull Player player) {
        ItemStack cost = getItemStack();
        if(cost != null)
            player.getInventory().removeItem(cost);
    }

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        return "Cost: " + type + " x" + count;
    }

    private @Nullable ItemStack getItemStack() {
        if(type == null) {
            QuickPartyLogger.error("No for PlayerCostItem.");
            return null;
        }
        try {
            return new ItemStack(
                    Material.valueOf(type.toUpperCase()),
                    count
            );
        } catch(Exception e) {
            QuickPartyLogger.error("Could not parse item-type '" + type + "' for PlayerCostItem.");
        }
        return null;
    }
}
