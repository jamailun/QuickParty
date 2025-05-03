package fr.jamailun.quickparty.listeners;

import fr.jamailun.quickparty.QuickPartyMain;
import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Cancel friendly fire, if needed
 */
public class FriendlyFireActionListener extends QpListener {
    public FriendlyFireActionListener(@NotNull QuickPartyMain plugin) {
        super(plugin);
    }

    @EventHandler
    void entityAttackEvent(@NotNull EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker))
            return;

        Party party = QuickParty.getPlayerParty(attacker);
        if(party == null || !party.hasMember(victim.getUniqueId())) return;

        event.setCancelled(!QuickPartyConfig.getInstance().isFriendlyFireEnabled());
    }

}
