package fr.jamailun.quickparty.configuration.parts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record TeleportModeSection(
        @Nullable Boolean enabled,
        @Nullable String permission,
        @Nullable Boolean needConfirmation,
        @Nullable Double teleportWaitSecs,
        @Nullable CostSection cost
) {

    static @NotNull TeleportModeSection empty() {
        return new TeleportModeSection(null, null, null, null, null);
    }

    public @NotNull TeleportModeSection withDefaults(@Nullable TeleportModeSection defaultValues) {
        TeleportModeSection bv = Objects.requireNonNullElse(defaultValues, empty());
        return new TeleportModeSection(
                enabled != null ? enabled : Objects.requireNonNullElse(bv.enabled, false),
                permission != null ? permission : Objects.requireNonNullElse(bv.permission, "none"),
                needConfirmation != null ? needConfirmation : Objects.requireNonNullElse(bv.needConfirmation, false),
                teleportWaitSecs != null ? teleportWaitSecs : Objects.requireNonNullElse(bv.teleportWaitSecs, 1d),
                cost != null ? cost : bv.cost
        );
    }

}
