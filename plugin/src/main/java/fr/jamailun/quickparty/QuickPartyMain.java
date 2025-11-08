package fr.jamailun.quickparty;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.QuickPartyPlugin;
import fr.jamailun.quickparty.api.parties.PartiesManager;
import fr.jamailun.quickparty.commands.PartyAdminCommand;
import fr.jamailun.quickparty.commands.PartyCommand;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.quickparty.expansions.UssAlliesCheckExpansion;
import fr.jamailun.quickparty.listeners.FriendlyFireActionListener;
import fr.jamailun.quickparty.listeners.JoinLeaveListener;
import fr.jamailun.quickparty.parties.PartiesManagerImpl;
import fr.jamailun.quickparty.expansions.QuickPartyPlaceholderExpansion;
import fr.jamailun.quickparty.utils.Metrics;
import fr.jamailun.ultimatespellsystem.api.providers.AlliesProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MobInvasion plugin implementation.
 */
@Getter
public final class QuickPartyMain extends JavaPlugin implements QuickPartyPlugin {

    private PartiesManager partiesManager;

    @Override
    public void onLoad() {
        QuickParty.setPlugin(this);
        QuickPartyScheduler.initialize(this);
    }

    @Override
    public void onEnable() {
        QuickPartyLogger.info("Enabling plugin.");

        // Configuration
        saveDefaultConfig();
        new QuickPartyConfig(this);

        // Managers
        partiesManager = new PartiesManagerImpl();

        // Commands
        new PartyCommand();
        new PartyAdminCommand();

        // Listeners
        new FriendlyFireActionListener(this);
        new JoinLeaveListener(this);

        registerExpansions();
    }

    @Override
    public void onDisable() {
        // ...
    }

    private void registerExpansions() {
        // Placeholder API
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            QuickPartyLogger.info("PlaceholderAPI not found. No expansion added.");
        } else {
            boolean success = new QuickPartyPlaceholderExpansion(getDescription().getVersion()).register();
            if(success) QuickPartyLogger.info("PlaceholderAPI extension registered.");
            else QuickPartyLogger.error("Could not register PlaceholderAPI extension.");
        }

        // USS
        if (Bukkit.getPluginManager().getPlugin("UltimateSpellSystem") == null) {
            QuickPartyLogger.info("Ultimate Spell System not found. No expansion added.");
        } else {
            AlliesProvider.instance().register(new UssAlliesCheckExpansion(), "quickparty");
            QuickPartyLogger.info("Ultimate Spell System extension registered.");
        }

        // bStats
        new Metrics(this, 25729);
    }

}
