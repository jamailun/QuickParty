package fr.jamailun.quickparty.configuration.parts;

import de.exlll.configlib.Comment;
import fr.jamailun.quickparty.api.parties.TeleportMode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record TeleportationSection(
    @Comment("Default config. Each mode will be able to override every property.")
    TeleportModeSection defaultMode,
    @Comment("The leader makes the call to TP every member to him.")
    Map<TeleportMode, TeleportModeSection> modes
) {
    public @NotNull TeleportModeSection completeFor(@NotNull TeleportMode mode) {
        TeleportModeSection base = modes.getOrDefault(mode, TeleportModeSection.empty());
        return base.withDefaults(defaultMode);
    }

    public TeleportationSection asValid() {
        return new TeleportationSection(
                TeleportModeSection.empty(),
                new HashMap<>()
        );
    }
}
