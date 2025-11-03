package fr.jamailun.quickparty.configuration.parts;

import de.exlll.configlib.Comment;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
        TeleportModeSection base = modes.computeIfAbsent(mode, x -> TeleportModeSection.empty());
        return base.withDefaults(defaultMode);
    }

    @Contract(" -> new")
    public @NotNull TeleportationSection asValid() {
        return new TeleportationSection(
                requestExpirationSeconds == null ? 30d : requestExpirationSeconds,
                defaultMode == null ? TeleportModeSection.empty() : defaultMode,
                modes == null ? new HashMap<>() : modes
        );
    }
}
