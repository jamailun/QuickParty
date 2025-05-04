package fr.jamailun.quickparty.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import fr.jamailun.quickparty.QuickPartyLogger;
import lombok.Getter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Plugin configuration file read.
 */
public class QuickPartyConfig {

    @Getter private static QuickPartyConfig instance;

    private final File dataFolder;
    private final File file;
    private final YamlConfigurationStore<MainConfiguration> store;
    private MainConfiguration config;

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private DateTimeFormatter datetimeFormat;

    // Translations
    private final Map<String, String> i18n = new HashMap<>();

    public QuickPartyConfig(@NotNull Plugin plugin) {
        dataFolder = plugin.getDataFolder();
        file = new File(dataFolder, "config.yml");

        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        store = new YamlConfigurationStore<>(MainConfiguration.class, properties);

        instance = this;

        reload();
    }

    public void reload() {
        // Clear
        datetimeFormat = null;
        i18n.clear();

        // Reload
        config = store.load(file.toPath());
        reloadLang();
    }

    private void reloadLang() {
        String lang = config.getLang();
        File file = new File(dataFolder, "lang/" + lang + ".yml");
        if(!file.exists()) {
            QuickPartyLogger.error("Translations file not found: " + file);
            return;
        }
        Configuration langConfig = YamlConfiguration.loadConfiguration(file);
        langConfig.getKeys(true).forEach(key -> i18n.put(key, langConfig.getString(key)));
    }

    public static @NotNull String getI18n(@NotNull String key) {
        return getInstance().i18n.getOrDefault(key, "?" + key + "?");
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
