package fr.jamailun.quickparty.expansions;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.invitations.PartyInvitation;
import fr.jamailun.quickparty.api.parties.PartyMember;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class QuickPartyPlaceholderExpansion extends PlaceholderExpansion {

    private final String version;

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
                        return getMember(party, index).getName();
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
                    if(index >= party.getMembers().size())
                        return "";
                    PartyMember member = getMember(party, index);
                    boolean isLeader = member.isPartyLeader();
                    boolean isSelf = member.getUUID().equals(player.getUniqueId());
                    boolean isOnline = member.isOnline();
                    String prefix = parse(member.getOfflinePlayer(), QuickPartyConfig.getInstance().getPrefix(isLeader, isSelf, isOnline));
                    String suffix = parse(member.getOfflinePlayer(), QuickPartyConfig.getInstance().getSuffix(isLeader, isSelf, isOnline));
                    return prefix + member.getName() + suffix;
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

    private @NotNull String parse(@NotNull OfflinePlayer player, @NotNull String raw) {
        String named = player.getName() == null ? raw : raw.replace("{NAME}", player.getName());
        if(PlaceholderAPI.containsPlaceholders(named))
            return PlaceholderAPI.setPlaceholders(player, named);
        return named;
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
        return version;
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

}
