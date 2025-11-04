package fr.jamailun.quickparty.costs;

import fr.jamailun.quickparty.api.cost.PlayerCost;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Cost to pay with real {@link ItemStack}.
 */
public record PlayerCostItem(
    Material type,
    int count
) implements PlayerCost {

    @Override
    public boolean canPay(@NotNull Player player) {
        ItemStack item = new ItemStack(type, count);
        return player.getInventory().containsAtLeast(item, item.getAmount());
    }

    @Override
    public void pay(@NotNull Player player) {
        ItemStack item = new ItemStack(type, count);
        player.getInventory().removeItem(item);
    }
}
