package fr.jamailun.quickparty.configuration.parts;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.cost.PlayerCost;

import java.util.Map;

/**
 * A cost definition.
 * @param type type the cost.
 * @param data data to deserialize.
 */
public record CostSection(
        String type,
        Map<String, Object> data
) {
    public PlayerCost deserialize() {
        return QuickParty.getCostsRegistry().deserialize(type, data);
    }
}
