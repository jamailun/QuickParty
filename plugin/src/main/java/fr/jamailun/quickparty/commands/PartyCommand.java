package fr.jamailun.quickparty.commands;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyInvitation;
import fr.jamailun.quickparty.api.parties.PartyInvitationResult;
import fr.jamailun.quickparty.api.parties.PartyMember;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PartyCommand extends CommandHelper implements CommandExecutor, TabCompleter {

    private static final String[] ARGS_INVITED = new String[] {"accept", "refuse"};
    private static final List<String> ARGS_WITHOUT_PARTY = List.of("invite");
    private static final List<String> ARGS_WITH_PARTY = listOf(ARGS_WITHOUT_PARTY, "info", "leave");
    private static final List<String> ARGS_PARTY_LEADER = listOf(ARGS_WITH_PARTY, "kick", "disband", "promote");

    public PartyCommand() {
        PluginCommand cmd = Bukkit.getPluginCommand("party");
        assert cmd != null;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull[] args) {
        if(!(sender instanceof Player player))
            return error(sender, i18n("need-to-be-player"));

        if("invite".equalsIgnoreCase(args[0])) {
            if(args.length < 2)
                return error(sender, i18n("missing-arg-player"));

            Player other = Bukkit.getPlayer(args[1]);
            if(other == null)
                return error(sender, i18n("unknown-player").replace("%player", args[1]));

            PartyInvitationResult result = QuickParty.getPartiesManager().invitePlayer(player, other);
            return switch (result.state()) {
                case PARTY_FULL -> error(player, i18n("invitation.party-full"));
                case PLAYER_ALREADY_IN_PARTY -> error(player, i18n("invitation.already-here"));
                case PARTY_CREATED -> success(player, i18n("invitation.success-create"));
                case INVITATION_SUCCESS -> success(player, i18n("invitation.success"));
            };
        }

        if("accept".equalsIgnoreCase(args[0])) {
            PartyInvitation invitation = QuickParty.getPartiesManager().getInvitationFor(player);
            if(invitation == null)
                return error(player, i18n("invitation.none"));
            invitation.getParty().join(player);
            return success(player, i18n("invitation.accepted"));
        }

        if("refuse".equalsIgnoreCase(args[0])) {
            PartyInvitation invitation = QuickParty.getPartiesManager().getInvitationFor(player);
            if(invitation == null)
                return error(player, i18n("invitation.none"));
            invitation.getParty().cancelInvitation(player.getUniqueId());
            return info(player, i18n("invitation.refused"));
        }

        Party party = QuickParty.getPlayerParty(player);
        if(party == null) {
            return info(player, i18n("no-party"));
        }
        PartyMember member = Objects.requireNonNull(party.getPartyMember(player.getUniqueId()), "Could not get role in party...");

        if("info".equalsIgnoreCase(args[0])) {
            String date = QuickPartyConfig.getInstance().getDatetimeFormat().format(party.getCreationDate());
            info(player, i18n("infos.intro").replace("%date", date));
            var members = party.getMembers();
            var invitations = party.getPendingInvitations();
            info(player, i18n("infos.members").replace("%size", ""+members.size()));
            for(PartyMember m : members) {
                String color = m.isPartyLeader() ? i18n("infos.member.color.leader") : i18n("infos.member.color.member");
                String name = (m.isPartyLeader() ? "&6" : "&a") + m.getOfflinePlayer().getName();
                String self = m.getUUID().equals(player.getUniqueId()) ?  i18n("infos.member.self") : "";
                info(player,
                        i18n("infos.member.line")
                                .replace("%player", name)
                                .replace("%color", color)
                                .replace("%self", self)
                );
            }

            if(!invitations.isEmpty()) {
                info(player, i18n("infos.invitations").replace("%size", ""+invitations.size()));
                for(PartyInvitation invitation : invitations) {
                    info(player,
                            i18n("infos.invitation-line")
                                .replace("%player", Objects.requireNonNullElseGet(invitation.getInvitedPlayer().getName(), () -> invitation.getInvitedPlayer().getUniqueId().toString()))
                                .replace("%date", QuickPartyConfig.getInstance().getDatetimeFormat().format(invitation.getDate()))
                    );
                }
            }
            return true;
        }

        if("disband".equalsIgnoreCase(args[0])) {
            if(!member.isPartyLeader())
                return error(sender, i18n("only-leader.disband"));
            party.disband();
            return true;
        }

        if("leave".equalsIgnoreCase(args[0])) {
            party.leave(player.getUniqueId());
            return true;
        }

        if(args.length < 2)
            return error(sender, i18n("missing-arg-player"));
        Player other = Bukkit.getPlayer(args[1]);
        if(other == null)
            return error(sender, i18n("unknown-player").replace("%player", args[1]));

        if("promote".equalsIgnoreCase(args[0])) {
            if(!member.isPartyLeader())
                return error(sender, i18n("only-leader.promote"));
            if(Objects.equals(other.getUniqueId(), player.getUniqueId()))
                return error(sender, i18n("promote-self"));
            party.promoteMember(other);
            return true;
        }

        if("kick".equalsIgnoreCase(args[0])) {
            if(!member.isPartyLeader())
                return error(sender, i18n("only-leader.kick"));
            if(Objects.equals(other.getUniqueId(), player.getUniqueId()))
                return error(sender, i18n("kick-self"));
            party.kick(other);
            return true;
        }
        return error(sender, i18n("unexpected").replace("%args", getFirstArgs(player).toString()));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull[] args) {
        if(!(sender instanceof Player player))
            return Collections.emptyList();

        if(args.length == 1) {
            String arg0 = args[0].toLowerCase();
            return getFirstArgs(player).stream()
                    .filter(s -> s.contains(arg0))
                    .toList();
        }

        if(args.length == 2) {
            String arg1 = args[1].toLowerCase();
            if("invite".equalsIgnoreCase(args[0])) {
                return Bukkit.getOnlinePlayers().stream()
                        .filter(p -> !Objects.equals(p.getUniqueId(), player.getUniqueId()))
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().contains(arg1))
                        .toList();
            }
            if("kick".equalsIgnoreCase(args[0]) || "promote".equalsIgnoreCase(args[0])) {
                Party party = QuickParty.getPlayerParty(player);
                if(party == null) return Collections.emptyList();
                return party.getMembers().stream()
                        .map(PartyMember::getOfflinePlayer)
                        .filter(op -> !Objects.equals(op.getUniqueId(), player.getUniqueId()))
                        .map(OfflinePlayer::getName)
                        .filter(Objects::nonNull)
                        .filter(n -> n.toLowerCase().contains(arg1))
                        .toList();
            }
        }

        return List.of();
    }

    private List<String> getFirstArgs(Player player) {
        String[] bonusInvited = QuickParty.getPartiesManager().hasInvitation(player) ? ARGS_INVITED : new String[0];

        Party party = QuickParty.getPlayerParty(player);
        if(party == null) return listOf(ARGS_WITHOUT_PARTY, bonusInvited);
        PartyMember member = party.getPartyMember(player.getUniqueId());
        return listOf((member != null && member.isPartyLeader()) ? ARGS_PARTY_LEADER : ARGS_WITH_PARTY, bonusInvited);
    }

}
