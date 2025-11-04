package fr.jamailun.quickparty.commands;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.cost.PlayerCost;
import fr.jamailun.quickparty.api.events.PartyPreTeleportEvent;
import fr.jamailun.quickparty.api.parties.*;
import fr.jamailun.quickparty.api.parties.invitations.PartyInvitation;
import fr.jamailun.quickparty.api.parties.invitations.PartyInvitationResult;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportRequest;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.quickparty.configuration.parts.TeleportModeSection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class PartyCommand extends CommandHelper implements CommandExecutor, TabCompleter {

    private static final String[] ARGS_INVITED = new String[] {"accept", "refuse"};
    private static final List<String> ARGS_WITHOUT_PARTY = List.of("invite");
    private static final String ARG_TP_ALL = "tpall";
    private static final String ARG_TP = "tp";
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
            // Teleport ?
            TeleportRequest request = QuickParty.getPartiesManager().getTeleportRequestFor(player);
            if(request != null) {
                request.accept();
                return true;
            }

            // Invitation
            PartyInvitation invitation = QuickParty.getPartiesManager().getInvitationFor(player);
            if(invitation == null)
                return error(player, i18n("invitation.none"));
            invitation.getParty().join(player);
            return success(player, i18n("invitation.accepted"));
        }

        if("refuse".equalsIgnoreCase(args[0])) {
            // Teleport ?
            TeleportRequest request = QuickParty.getPartiesManager().getTeleportRequestFor(player);
            if(request != null) {
                request.cancel();
                return true;
            }

            // Invitation
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
        PartyMember member = Objects.requireNonNull(party.getPartyMember(player), "Could not get role in party...");

        if("info".equalsIgnoreCase(args[0])) {
            String date = QuickPartyConfig.getInstance().getDatetimeFormat().format(party.getCreationDate());
            info(player, i18n("infos.intro").replace("%date", date));
            var members = party.getMembers();
            var invitations = party.getPendingInvitations();
            info(player, i18n("infos.members").replace("%size", ""+members.size()));
            for(PartyMember m : members) {
                String color = m.isPartyLeader() ? i18n("infos.member.color.leader") : i18n("infos.member.color.member");
                String name = (m.isPartyLeader() ? "&6" : "&a") + m.getName();
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

        if(ARG_TP_ALL.equalsIgnoreCase(args[0])) {
            if(!member.isPartyLeader())
                return error(sender, i18n("only-leader.tpall"));

            TeleportModeSection config = QuickPartyConfig.getInstance().getTeleportRules(TeleportMode.ALL_TO_LEADER);
            if(config.disabled())
                return error(sender, i18n("teleport.mode-disabled.all-to-leader"));
            PlayerCost cost = config.getCost();
            if(cost != null && !cost.canPay(player))
                return error(sender, i18n("teleport.cannot-pay"));

            int count = 0;
            for(PartyMember other : party.getMembers()) {
                if(other.equals(member) || other.isOnline()) continue;
                PartyPreTeleportEvent event = new PartyPreTeleportEvent(party, other.getOnlinePlayer(), player, TeleportMode.ALL_TO_LEADER);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    other.sendTeleportRequest(player, TeleportMode.ALL_TO_LEADER);
                    count++;
                }
            }

            if(count > 0)
                return success(sender, i18n("teleport.send-success.tpall").replace("%count", String.valueOf(count)));
            return error(sender, i18n("teleport.no-members-tpall"));
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

        if(ARG_TP.equalsIgnoreCase(args[0])) {
            PartyMember target = party.getPartyMember(other);
            if(target == null) return error(sender, i18n("teleport.not-in-party"));

            // Evaluate mode
            TeleportMode mode = TeleportMode.evaluateWith(member, target);
            TeleportModeSection config = QuickPartyConfig.getInstance().getTeleportRules(mode);
            if(config.disabled()) {
                return switch (mode) {
                    case ALL_TO_LEADER -> throw new UnsupportedOperationException("Cannot be here.");
                    case LEADER_TO_MEMBER -> error(sender, i18n("teleport.mode-disabled.leader-to-member"));
                    case MEMBER_TO_LEADER -> error(sender, i18n("teleport.mode-disabled.member-to-leader"));
                    case MEMBER_TO_MEMBER -> error(sender, i18n("teleport.mode-disabled.member-to-member"));
                };
            }

            // Send request
            member.sendTeleportRequest(other, mode);
            return success(sender, i18n("teleport.send-success"));
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
                Set<UUID> existing = new HashSet<>();
                Party party = QuickParty.getPlayerParty(player);
                if(party != null) {
                    party.getMembers().forEach(m -> existing.add(m.getUUID()));
                    party.getPendingInvitations().forEach(i -> existing.add(i.getInvitedPlayer().getUniqueId()));
                } else {
                    existing.add(player.getUniqueId());
                }
                return Bukkit.getOnlinePlayers().stream()
                        .filter(p -> !existing.contains(p.getUniqueId()))
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
            if(ARG_TP.equalsIgnoreCase(args[0])) {
                Party party = QuickParty.getPlayerParty(player);
                if(party == null) return Collections.emptyList();
                PartyMember member = Objects.requireNonNull(party.getPartyMember(player.getUniqueId()));
                Set<TeleportMode> enabledModes = QuickPartyConfig.getInstance().getEnabledTeleportModes();
                boolean acceptsLeader = enabledModes.contains(TeleportMode.MEMBER_TO_LEADER);
                boolean acceptsMember = member.isPartyLeader() && enabledModes.contains(TeleportMode.LEADER_TO_MEMBER);
                boolean acceptsAll = enabledModes.contains(TeleportMode.MEMBER_TO_MEMBER);

                return party.getMembers().stream()
                        // Online and not sender
                        .filter(mem -> !mem.getUUID().equals(player.getUniqueId()) && mem.isOnline())
                        // Must match flags
                        .filter(mem -> {
                            if(mem.isPartyLeader()) return acceptsLeader;
                            return acceptsMember || acceptsAll;
                        })
                        // Map and filter with name
                        .map(mem -> mem.getOnlinePlayer().getName())
                        .filter(n -> n.toLowerCase().contains(arg1))
                        .toList();
            }
        }

        return List.of();
    }

    private @NotNull @Unmodifiable List<String> getFirstArgs(@NotNull Player player) {
        // Add 'refuse' and 'accept' if an invitation/tp-request exists.
        boolean hasTpRequest = QuickParty.getPartiesManager().hasTeleportRequest(player);
        boolean hasInvitation = QuickParty.getPartiesManager().hasInvitation(player);
        String[] bonusInvited = (hasTpRequest || hasInvitation) ? ARGS_INVITED : new String[0];

        // Has party ?
        Party party = QuickParty.getPlayerParty(player);
        if(party == null) return listOf(ARGS_WITHOUT_PARTY, bonusInvited);
        PartyMember member = Objects.requireNonNull(party.getPartyMember(player));
        List<String> baseCommands = listOf(member.isPartyLeader() ? ARGS_PARTY_LEADER : ARGS_WITH_PARTY, bonusInvited);

        // No other player ? Don't check tp args...
        if(party.getMembers().size() == 1)
            return baseCommands;

        // Add TP if mode are enabled
        Set<TeleportMode> enabledModes = QuickPartyConfig.getInstance().getEnabledTeleportModes();
        boolean canTp = enabledModes.contains(TeleportMode.MEMBER_TO_MEMBER)
                || (!member.isPartyLeader() && enabledModes.contains(TeleportMode.MEMBER_TO_LEADER))
                || (member.isPartyLeader() && enabledModes.contains(TeleportMode.LEADER_TO_MEMBER));
        boolean canTpAll = member.isPartyLeader() && enabledModes.contains(TeleportMode.ALL_TO_LEADER);

        // Concatenate with arguments
        return listOf(
                baseCommands,
                canTp ? ARG_TP : null,
                canTpAll ? ARG_TP_ALL : null
        );
    }

}
