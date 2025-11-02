package fr.jamailun.quickparty.api.cost;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Deserializer for a {@link PlayerCost}.
 */
public interface CostDeserializer {

    /**
     * Deserialize a data-map.
     * @param type ID of this cost.
     * @param data data provided by the configuration.
     * @return deserialized cost.
     */
    @NotNull PlayerCost deserialize(@NotNull String type, @NotNull Map<String, Object> data);

}
