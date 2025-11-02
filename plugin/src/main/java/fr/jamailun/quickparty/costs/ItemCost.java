package fr.jamailun.quickparty.costs;

import fr.jamailun.quickparty.api.cost.PlayerCost;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Cost to pay with items.
 */
public class ItemCost implements PlayerCost {

    private final ItemStack cost;

    public ItemCost(@NotNull Map<String, Object> data) {
        if(data.containsKey("type")) {
            String type = (String) data.get("type");
            int count = (int) data.getOrDefault("count", 1);
            cost = new ItemStack(Material.valueOf(type), count);
        } else {
            cost = ItemStack.deserialize(data);
        }
    }

    @Override
    public boolean canPay(@NotNull Player player) {
        return player.getInventory().containsAtLeast(cost, cost.getAmount());
    }

    @Override
    public void pay(@NotNull Player player) {
        player.getInventory().removeItem(cost);
    }
}
