package fr.jamailun.quickparty.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import fr.jamailun.quickparty.QuickPartyLogger;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Plugin configuration file read.
 */
public class QuickPartyConfig {

    @Getter private static QuickPartyConfig instance;

    private final File file;
    private final YamlConfigurationStore<MainConfiguration> store;
    private MainConfiguration config;

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private DateTimeFormatter datetimeFormat;

    public QuickPartyConfig(@NotNull Plugin plugin) {
        file = new File(plugin.getDataFolder(), "config.yml");

        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        store = new YamlConfigurationStore<>(MainConfiguration.class, properties);

        instance = this;

        reload();
    }

    public void reload() {
        // Clear
        datetimeFormat = null;

        // Reload
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

    public @NotNull DateTimeFormatter getDatetimeFormat() {
        if(datetimeFormat == null) {
            try {
                datetimeFormat = DateTimeFormatter.ofPattern(config.getFormat().dateTime());
            } catch(IllegalArgumentException e) {
                QuickPartyLogger.error("Invalid datetime format ('"+config.getFormat().dateTime()+"'). Will use the default format instead.");
                datetimeFormat = DEFAULT_FORMAT;
            }
        }
        return datetimeFormat;
    }

}
