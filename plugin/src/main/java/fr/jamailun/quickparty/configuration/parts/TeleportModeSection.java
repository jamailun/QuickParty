package fr.jamailun.quickparty.configuration.parts;

import fr.jamailun.quickparty.api.cost.PlayerCost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record TeleportModeSection(
        Boolean enabled,
        String permission,
        Boolean needConfirmation,
        Double teleportWaitSecs,
        CostSection cost
) {

    public boolean disabled() {
        return ! Objects.requireNonNullElse(enabled, false);
    }

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

    public @Nullable PlayerCost getCost() {
        return cost == null ? null : cost.deserialize();
    }

}
