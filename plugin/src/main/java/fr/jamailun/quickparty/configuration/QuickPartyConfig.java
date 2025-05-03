package fr.jamailun.quickparty.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Plugin configuration file read.
 */
public class QuickPartyConfig {

    private final File file;
    private final YamlConfigurationStore<MainConfiguration> store;
    private MainConfiguration config;

    @Getter private static QuickPartyConfig instance;

    public QuickPartyConfig(@NotNull Plugin plugin) {
        file = new File(plugin.getDataFolder(), "config.yml");

        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        store = new YamlConfigurationStore<>(MainConfiguration.class, properties);

        instance = this;
    }

    public void reload() {
        config = store.load(file.toPath());
    }

    public boolean isDebug() {
        return config.isDebug();
    }

    public int getMaxPartySize() {
        return config.getParties().maxSize();
    }

    public boolean isFriendlyFireEnabled() {
        return config.getParties().friendlyFire();
    }

}
