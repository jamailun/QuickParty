package fr.jamailun.quickparty.expansions;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;

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

        Party party =  QuickParty.getPlayerParty(player);
        if(param.equals("has_party"))
            return bool(party != null);
        if(party == null) return "";

        return switch (param) {
            case "party_leader" -> party.getLeader().getOfflinePlayer().getName();
            case "party_is_leader" -> bool(party.getLeader().getUUID().equals(player.getUniqueId()));
            case "party_creation_date" -> DateTimeFormatter.ISO_DATE_TIME.format(party.getCreationDate());
            case "party_size" -> String.valueOf(party.getSize());
            case "party_size_members" -> String.valueOf(party.getMembers().size());
            case "party_size_invitations" -> String.valueOf(party.getPendingInvitations().size());
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

}
