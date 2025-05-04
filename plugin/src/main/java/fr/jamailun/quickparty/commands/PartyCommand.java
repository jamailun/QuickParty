package fr.jamailun.quickparty.commands;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyInvitation;
import fr.jamailun.quickparty.api.parties.PartyInvitationResult;
import fr.jamailun.quickparty.api.parties.PartyMember;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PartyCommand extends CommandHelper implements CommandExecutor, TabCompleter {

    private static final List<String> ARGS_WITHOUT_PARTY = List.of("invite", "accept", "refuse");
    private static final List<String> ARGS_WITH_PARTY = listOf(ARGS_WITHOUT_PARTY, "info", "leave");
    private static final List<String> ARGS_PARTY_LEADER = listOf(ARGS_WITH_PARTY, "kick", "disband", "promote");

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public PartyCommand() {
        PluginCommand cmd = Bukkit.getPluginCommand("party");
        assert cmd != null;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull[] args) {
        if(!(sender instanceof Player player))
            return error(sender, "You need to be a player.");

        if("invite".equalsIgnoreCase(args[0])) {
            if(args.length < 2)
                return error(sender, "Missing argument : you need to specify the player to invite.");

            Player other = Bukkit.getPlayer(args[1]);
            if(other == null)
                return error(sender, "Unknown player '&4" + args[1] + "&r'. Is he online ?");

            PartyInvitationResult result = QuickParty.getPartiesManager().invitePlayer(player, other);
            return switch (result.state()) {
                case PARTY_FULL -> error(player, "The party is already full.");
                case PLAYER_ALREADY_IN_PARTY -> error(player, "This player is already in your party.");
                case PARTY_CREATED -> success(player, "Invitation sent, party successfully created.");
                case INVITATION_SUCCESS -> success(player, "Invitation sent.");
            };
        }

        if("accept".equalsIgnoreCase(args[0])) {
            PartyInvitation invitation = QuickParty.getPartiesManager().getInvitationFor(player);
            if(invitation == null)
                return error(player, "You don't have any pending invitation.");
            invitation.getParty().join(player);
            return success(player, "You have join the party.");
        }

        if("refuse".equalsIgnoreCase(args[0])) {
            PartyInvitation invitation = QuickParty.getPartiesManager().getInvitationFor(player);
            if(invitation == null)
                return error(player, "You don't have any pending invitation.");
            invitation.getParty().cancelInvitation(player.getUniqueId());
            return info(player, "You denied the party invitation.");
        }

        Party party = QuickParty.getPlayerParty(player);
        if(party == null) {
            return info(player, "You are not in a party.");
        }
        PartyMember member = Objects.requireNonNull(party.getPartyMember(player.getUniqueId()), "Could not get role in party...");

        if("info".equalsIgnoreCase(args[0])) {
            info(player, "Party created at &b" + DATE_FORMAT.format(party.getCreationDate()));
            var members = party.getMembers();
            var invitations = party.getPendingInvitations();
            info(player, "Party members (&b"+members.size()+") :");
            for(PartyMember m : members) {
                String name = (m.isPartyLeader() ? "&6" : "&a") + m.getOfflinePlayer().getName();
                String self = m.getUUID().equals(player.getUniqueId()) ? " &b(you)" : "";
                info(player, "- " + name + self);
            }

            if(!invitations.isEmpty()) {
                info(player, "Party invitations (&b"+invitations.size()+") :");
                for(PartyInvitation invitation : invitations) {
                    info(player, "- &7" + invitation.getInvitedPlayer().getName());
                }
            }
            return true;
        }

        if("disband".equalsIgnoreCase(args[0])) {
            if(!member.isPartyLeader())
                return error(sender, "Only the party leader can disband the party !");
            party.disband();
            return success(player, "Party successfully disbanded.");
        }

        if("leave".equalsIgnoreCase(args[0])) {
            party.leave(player.getUniqueId());
            return success(sender, "You left the party.");
        }

        if(args.length < 2)
            return error(sender, "Missing argument : you need to specify the player.");
        Player other = Bukkit.getPlayer(args[1]);
        if(other == null)
            return error(sender, "Unknown player '&4" + args[1] + "&r'. Is he online ?");

        if("promote".equalsIgnoreCase(args[0])) {
            if(!member.isPartyLeader())
                return error(sender, "Only the party leader can promote a member !");
            if(!Objects.equals(other.getUniqueId(), player.getUniqueId()))
                return error(sender, "You want to promote yourself ?");
            party.promoteMember(other);
            return success(sender, "Successfully promoted &e" + other.getName() + "&r as the new party leader.");
        }

        if("kick".equalsIgnoreCase(args[0])) {
            if(!member.isPartyLeader())
                return error(sender, "Only the party leader can kick a member !");
            if(!Objects.equals(other.getUniqueId(), player.getUniqueId()))
                return error(sender, "You want to kick yourself ? Please, disband the party instead.");
            party.kick(other);
            return success(sender, "Successfully promoted &e" + other.getName() + "&r as the new party leader.");
        }


        return error(sender, "Not reachable.");
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
        Party party = QuickParty.getPlayerParty(player);
        if(party == null) return ARGS_WITHOUT_PARTY;
        PartyMember member = party.getPartyMember(player.getUniqueId());
        return (member != null && member.isPartyLeader()) ? ARGS_PARTY_LEADER : ARGS_WITH_PARTY ;
    }

}
