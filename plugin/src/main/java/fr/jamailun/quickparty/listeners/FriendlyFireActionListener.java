package fr.jamailun.quickparty.listeners;

import fr.jamailun.quickparty.QuickPartyMain;
import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.quickparty.utils.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Cancel friendly fire, if needed.
 */
public class FriendlyFireActionListener extends QpListener {
    public FriendlyFireActionListener(@NotNull QuickPartyMain plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    void entityAttackEvent(@NotNull EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Projectile projectile) {
            if(projectile.getShooter() instanceof Entity shooter) {
                cancelIfNeeded(event, shooter);
            }
            return;
        }

        cancelIfNeeded(event, event.getDamager());
    }

    private void cancelIfNeeded(@NotNull EntityDamageByEntityEvent event, @NotNull Entity attacker) {
        Entity target = event.getEntity();
        if(shouldCancel(attacker, target)) {
            event.setCancelled(true);
            attacker.sendMessage(StringUtils.parseString("&c" + QuickPartyConfig.getI18n("players.cannot-damage")));
        }
    }

    private boolean shouldCancel(@NotNull Entity attacker, @NotNull Entity target) {
        Party party = QuickParty.getPlayerParty(attacker.getUniqueId());
        if(party == null || !party.hasMember(target.getUniqueId())) return false;
        return ! QuickPartyConfig.getInstance().isFriendlyFireEnabled();
    }

}
