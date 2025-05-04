package fr.jamailun.quickparty;

import fr.jamailun.quickparty.api.QuickParty;
import fr.jamailun.quickparty.api.QuickPartyPlugin;
import fr.jamailun.quickparty.api.parties.PartiesManager;
import fr.jamailun.quickparty.commands.PartyCommand;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.quickparty.expansions.UssAlliesCheckExpansion;
import fr.jamailun.quickparty.listeners.FriendlyFireActionListener;
import fr.jamailun.quickparty.parties.PartiesManagerImpl;
import fr.jamailun.quickparty.expansions.QuickPartyPlaceholderExpansion;
import fr.jamailun.quickparty.utils.Metrics;
import fr.jamailun.ultimatespellsystem.api.UltimateSpellSystem;
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
        QuickPartyLogger.initialize(this);
    }

    @Override
    public void onEnable() {
        QuickPartyLogger.info("Enabling plugin.");

        // default config
        saveDefaultConfig();
        new QuickPartyConfig(this);

        // Managers
        partiesManager = new PartiesManagerImpl();

        // Commands
        new PartyCommand();

        // Listeners
        new FriendlyFireActionListener(this);

        registerExpansions();
    }

    @Override
    public void onDisable() {
        // ...
    }

    @SuppressWarnings("UnstableApiUsage")
    private void registerExpansions() {
        // Placeholder API
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            QuickPartyLogger.info("PlaceholderAPI not found. No expansion added.");
        } else {
            boolean success = new QuickPartyPlaceholderExpansion(getPluginMeta()).register();
            if(success) QuickPartyLogger.info("PlaceholderAPI extension registered.");
            else QuickPartyLogger.error("Could not register PlaceholderAPI extension.");
        }

        // USS
        if(UltimateSpellSystem.isValid()) {
            AlliesProvider.instance().register(new UssAlliesCheckExpansion(), "quickparty");
            QuickPartyLogger.info("Ultimate Spell System extension registered.");
        } else {
            QuickPartyLogger.info("Ultimate Spell System not found. No expansion added.");
        }

        // bStats
        new Metrics(this, 25729);
    }

}
