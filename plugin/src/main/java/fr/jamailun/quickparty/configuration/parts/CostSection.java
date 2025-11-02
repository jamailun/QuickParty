package fr.jamailun.quickparty.configuration.parts;

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
}
