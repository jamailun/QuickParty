package fr.jamailun.quickparty.expansions;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.ultimatespellsystem.api.entities.SpellEntity;
import fr.jamailun.ultimatespellsystem.api.providers.AlliesProvider;
import fr.jamailun.ultimatespellsystem.api.providers.AlliesProvider.AlliesResult;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class UssAlliesCheckExpansion implements AlliesProvider.AlliesCheck {

    @Override
    public @NotNull AlliesResult test(@NotNull SpellEntity caster, @NotNull Entity target) {
        Party party = QuickParty.getPlayerParty(caster.getUniqueId());
        if(party == null || !party.hasMember(target.getUniqueId())) return AlliesResult.IGNORE;

        return QuickPartyConfig.getInstance().isFriendlyFireEnabled() ? AlliesResult.IGNORE : AlliesResult.ALLIES;
    }

}
