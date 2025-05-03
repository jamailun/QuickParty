package fr.jamailun.quickparty.placeholder;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor
public class QuickPartyPlaceholderExpansion extends PlaceholderExpansion {

    private final PluginMeta meta;

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String param) {
        if(player == null) {
            // Specific
            return "";
        }

        return switch (param) {
            case "has_party" -> bool(partyOf(player) != null);
            default -> "";
        };
    }

    @Override
    public @NotNull String getIdentifier() {
        return "qpa";
    }

    @Override
    public @NotNull String getAuthor() {
        return "jamailun";
    }

    @Override
    public @NotNull String getVersion() {
        return meta.getVersion();
    }

    private @NotNull String bool(boolean condition) {
        return condition ? "true" : "false";
    }

    private @Nullable Party partyOf(@NotNull Player player) {
        return QuickParty.getPlayerParty(player).orElse(null);
    }

}
