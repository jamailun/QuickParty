package fr.jamailun.quickparty;

import fr.jamailun.quickparty.api.QuickPartyPlugin;
import fr.jamailun.quickparty.api.parties.PartiesManager;
import fr.jamailun.quickparty.commands.PartyCommand;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import fr.jamailun.quickparty.listeners.FriendlyFireActionListener;
import fr.jamailun.quickparty.parties.PartiesManagerImpl;
import fr.jamailun.quickparty.placeholder.QuickPartyPlaceholderExpansion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MobInvasion plugin implementation.
 */
public final class QuickPartyMain extends JavaPlugin implements QuickPartyPlugin {

    @Getter private PartiesManager partiesManager;


    @Override
    public void onLoad() {
        QuickPartyLogger.initialize(this, "&b[QP]&r ");
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
            QuickPartyLogger.warn("Could not find PlaceholderAPI.");
        } else {
            boolean success = new QuickPartyPlaceholderExpansion(getPluginMeta()).register();
            if(success) QuickPartyLogger.info("PlaceholderAPI extension registered.");
            else QuickPartyLogger.error("Could not register PlaceholderAPI extension.");
        }
    }

}
