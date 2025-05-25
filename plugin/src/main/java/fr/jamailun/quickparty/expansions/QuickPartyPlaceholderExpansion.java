package fr.jamailun.quickparty.expansions;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyInvitation;
import fr.jamailun.quickparty.api.parties.PartyMember;
import fr.jamailun.quickparty.configuration.PrefixReferential;
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

    private static final Pattern MEMBER_NUM_EXTRACTOR = Pattern.compile("party_(nice_member|member|invitation|invit|is_leader|color)_([1-9][0-9]*)");

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String param) {
        if(player == null) {
            // Specific
            return "";
        }

        Party party =  QuickParty.getPlayerParty(player);
        if(param.equals("has_party"))
            return bool(party != null && party.hasMember(player.getUniqueId()));
        if(party == null) return param.contains("size") ? "0" : "";

        // Party num ?
        Matcher partyNumExt = MEMBER_NUM_EXTRACTOR.matcher(param);
        if(partyNumExt.matches()) {
            int index = Integer.parseInt(partyNumExt.group(2)) - 1;
            switch (partyNumExt.group(1)) {
                case "member" -> {
                    if(index < party.getMembers().size())
                        return getMember(party, index).getOfflinePlayer().getName();
                }
                case "is_leader" -> {
                    if(index < party.getMembers().size())
                        return bool(getMember(party, index).isPartyLeader());
                }
                case "color" -> {
                    if(index < party.getMembers().size())
                        return getMember(party, index).isPartyLeader() ? "&6" : "";
                }
                case "invitation", "invit" -> {
                    if(index < party.getPendingInvitations().size())
                        return getInvitation(party, index).getInvitedPlayer().getName();
                }
                case "nice_member" -> {
                    if(index < party.getMembers().size()) {
                        PartyMember member = getMember(party, index);
                        PrefixReferential ref = of(player, member);
                        String prefix = QuickPartyConfig.getInstance().getPrefix(ref, member.isOnline());
                        String suffix = QuickPartyConfig.getInstance().getPrefix(ref, member.isOnline());
                        return prefix + member.getOfflinePlayer().getName() + suffix;
                    }
                }
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
            default -> "null";
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

    private @NotNull PartyMember getMember(Party party, int index) {
        return List.copyOf(party.getMembers()).get(index);
    }
    private @NotNull PartyInvitation getInvitation(Party party, int index) {
        return List.copyOf(party.getPendingInvitations()).get(index);
    }
    private PrefixReferential of(Player self, PartyMember other) {
        if(self.getUniqueId().equals(other.getUUID())) return PrefixReferential.SELF;
        return other.isPartyLeader() ? PrefixReferential.LEADER : PrefixReferential.MEMBER;
    }

}
