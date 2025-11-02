package fr.jamailun.quickparty.costs;

import fr.jamailun.quickparty.QuickPartyLogger;
import fr.jamailun.quickparty.api.cost.CostDeserializer;
import fr.jamailun.quickparty.api.cost.PlayerCost;
import fr.jamailun.quickparty.api.cost.PlayerCostsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CostRegistryImpl implements PlayerCostsRegistry {

    private final Map<String, CostDeserializer> deserializers = new HashMap<>();

    public void registerBasics() {
        register("item", (x, data) -> new ItemCost(data));
    }

    @Override
    public void register(@NotNull String id, @NotNull CostDeserializer deserializer) {
        deserializers.put(id, deserializer);
    }

    @Override
    public @Nullable PlayerCost deserialize(@NotNull String id, @NotNull Map<String, Object> data) {
        CostDeserializer deserializer = deserializers.get(id);
        if (deserializer == null) return null;
        try {
            return deserializer.deserialize(id, data);
        } catch (Exception e) {
            QuickPartyLogger.error("Could not deserialize cost '" + id + "'.", e);
            return null;
        }
    }
}
