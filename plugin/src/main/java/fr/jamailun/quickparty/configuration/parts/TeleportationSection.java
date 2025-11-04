package fr.jamailun.quickparty.configuration.parts;

import de.exlll.configlib.Comment;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public record TeleportationSection(
    @Comment({"Duration (in seconds) before a teleport request expires.",
            "Remember, in the 'ALL_TO_LEADER' case, the leader pre-pays teleportation.",
            "Don't make it too short to not anger the players ! :)"})
    Double requestExpirationSeconds,
    @Comment("Default config. Each mode will be able to override every property.")
    TeleportModeSection defaultMode,
    @Comment("The leader makes the call to TP every member to him.")
    Map<TeleportMode, TeleportModeSection> modes
) {

    public @NotNull TeleportModeSection completeFor(@NotNull TeleportMode mode) {
        return modes.get(mode).withDefaults(defaultMode);
    }

    @Contract(" -> new")
    public @NotNull TeleportationSection asValid() {
        Map<TeleportMode, TeleportModeSection> modesData =
                Objects.requireNonNullElseGet(modes, () -> new EnumMap<>(TeleportMode.class));
        Arrays.stream(TeleportMode.values())
                .forEach(m -> modesData.putIfAbsent(m, TeleportModeSection.empty()));

        return new TeleportationSection(
                requestExpirationSeconds == null ? 30d : requestExpirationSeconds,
                defaultMode == null ? TeleportModeSection.defaultValues() : defaultMode,
                modesData
        );
    }

    public @NotNull Set<TeleportMode> getEnabledModes() {
        return modes.entrySet().stream()
                .filter(e -> e.getValue().enabled())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
