package fr.jamailun.quickparty.api.cost;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A registry for {@link CostDeserializer}.
 */
public interface PlayerCostsRegistry {

    /**
     * Register a new {@link PlayerCost} deserializer.
     * @param id ID of the cost.
     * @param deserializer instance to register.
     */
    void register(@NotNull String id, @NotNull CostDeserializer deserializer);

    /**
     * Try to deserialize an element.
     * @param id ID of the {@link PlayerCost}.
     * @param data data to deserialize.
     * @return {@code null} if no deserializer could be found.
     */
    @Nullable PlayerCost deserialize(@NotNull String id, @NotNull Map<String, Object> data);

}
