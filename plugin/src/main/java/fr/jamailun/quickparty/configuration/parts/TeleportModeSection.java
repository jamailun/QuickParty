package fr.jamailun.quickparty.configuration.parts;

import fr.jamailun.quickparty.api.cost.PlayerCost;
import fr.jamailun.quickparty.costs.PlayerCostItem;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record TeleportModeSection(
        boolean enabled,
        String permission,
        Boolean needConfirmation,
        Double teleportWaitSecs,
        // Allowed cost
        PlayerCostItem costItem
) {

    public boolean disabled() {
        return ! enabled;
    }

    static @NotNull TeleportModeSection empty() {
        return new TeleportModeSection(false, null, null, null, null);
    }
    static @NotNull TeleportModeSection defaultValues() {
        return new TeleportModeSection(false, "none", false, 1d, null);
    }

    public boolean doesNeedConfirmation() {
        return Objects.requireNonNullElse(needConfirmation, true);
    }

    public @NotNull TeleportModeSection withDefaults(@NotNull TeleportModeSection dv) {
        return new TeleportModeSection(
                enabled,
                permission != null ? permission : Objects.requireNonNullElse(dv.permission, "none"),
                needConfirmation != null ? needConfirmation : Objects.requireNonNullElse(dv.needConfirmation, false),
                teleportWaitSecs != null ? teleportWaitSecs : Objects.requireNonNullElse(dv.teleportWaitSecs, 1d),
                costItem != null ? costItem : dv.costItem
        );
    }

    public @Nullable PlayerCost getCost() {
        //TODO add other costs later.
        return costItem;
    }

    /**
     * Test if a command-sender has the proper permission.
     * @param sender cmd-sender to test.
     * @return true when can do tp.
     */
    public boolean hasPermission(@NotNull CommandSender sender) {
        return permission == null
                || permission.isBlank()
                || "none".equals(permission)
                || sender.hasPermission(permission);
    }
}
