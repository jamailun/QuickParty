package fr.jamailun.quickparty.listeners;

import fr.jamailun.quickparty.QuickPartyMain;
import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyMember;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class JoinLeaveListener extends QpListener {
    public JoinLeaveListener(@NotNull QuickPartyMain plugin) {
        super(plugin);
    }

    @EventHandler
    void playerJoin(@NotNull PlayerJoinEvent event) {
        Party party = QuickParty.getPlayerParty(event.getPlayer());
        if(party == null) return;
        PartyMember member = party.getPartyMember(event.getPlayer());
        if(member != null) {
            member.refreshOnline();
        }
    }
}
