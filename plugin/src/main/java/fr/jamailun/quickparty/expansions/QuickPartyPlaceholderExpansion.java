package fr.jamailun.quickparty.expansions;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor
public class QuickPartyPlaceholderExpansion extends PlaceholderExpansion {

    private final PluginMeta meta;

    private static final Pattern MEMBER_NUM_EXTRACTOR = Pattern.compile("party_(member|invitation)_([1-9][0-9]*)");

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

        // Party num ?
        Matcher partyNumExt = MEMBER_NUM_EXTRACTOR.matcher(param);
        if(partyNumExt.matches()) {
            int index = Integer.parseInt(partyNumExt.group(2));
            boolean member = "member".equals(partyNumExt.group(1));
            if(member) {
                if(index < party.getMembers().size())
                    return List.copyOf(party.getMembers()).get(index).getOfflinePlayer().getName();
            } else {
                if(index < party.getPendingInvitations().size())
                    return List.copyOf(party.getPendingInvitations()).get(index).getInvitedPlayer().getName();
            }
            return "&cOutOfBound";
        }

        return switch (param) {
            case "party_leader" -> party.getLeader().getOfflinePlayer().getName();
            case "party_is_leader" -> bool(party.getLeader().getUUID().equals(player.getUniqueId()));
            case "party_creation_date" -> QuickPartyConfig.getInstance().getDatetimeFormat().format(party.getCreationDate());
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
