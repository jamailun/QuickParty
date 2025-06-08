package fr.jamailun.quickparty.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import fr.jamailun.quickparty.QuickPartyLogger;
import fr.jamailun.quickparty.utils.JarReader;
import fr.jamailun.quickparty.utils.StringUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Getter public static boolean isDebug = false;

    // Translations
    private final Map<String, String> i18n = new HashMap<>();
    private final List<String> messageI18n = new ArrayList<>();

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
        messageI18n.clear();

        // Reload
        config = store.load(file.toPath());
        config.checkValidity();
        store.save(config, file.toPath());

        isDebug = config.isDebug();

        reloadLang();
    }

    private void reloadLang() {
        String lang = config.getLang();
        File langDir = new File(dataFolder, "lang");
        if(!langDir.exists()) {
            QuickPartyLogger.info("No lang/ directory. Will create it.");
            if(!langDir.mkdirs()) {
                QuickPartyLogger.error("Could not create lang/ directory.");
                return;
            }
        }
        // Extract lang files when langs/ dir is empty.
        if(Objects.requireNonNull(langDir.listFiles()).length == 0) {
            extractLangFiles(langDir);
        }
        File file = new File(dataFolder, "lang/" + lang + ".yml");
        if(!file.exists()) {
            QuickPartyLogger.error("Translations file not found: " + file);
            return;
        }
        Configuration langConfig = YamlConfiguration.loadConfiguration(file);
        for(String key : langConfig.getKeys(true)) {
            if("players.invitation.message".equalsIgnoreCase(key)) {
                messageI18n.addAll(langConfig.getStringList(key));
            } else {
                i18n.put(key, langConfig.getString(key));
            }
        }
    }

    private static @NotNull Component prepareComplexMessage(@NotNull String raw) {
        List<String> parts = StringUtils.splitWithDelimiters(raw, "%CMD_ACCEPT", "%CMD_REFUSE");
        List<Component> components = new ArrayList<>();
        for(String part : parts) {
            components.add(switch (part) {
                case "%CMD_ACCEPT" -> Component.text("/p accept", NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/p accept"));
                case "%CMD_REFUSE" -> Component.text("/p refuse", NamedTextColor.RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/p refuse"));
                default -> StringUtils.parseString(part);
            });
        }
        return Component.join(JoinConfiguration.noSeparators(), components);
    }

    public static @NotNull String getI18n(@NotNull String key) {
        return getInstance().i18n.getOrDefault(key, "?" + key + "?");
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

    public void sendMessageTo(@NotNull Player inviter, @NotNull CommandSender target) {
        for(String line : messageI18n) {
            // Replace
            String replaced = line.replace("%player", inviter.getName());
            // Transform
            Component component = prepareComplexMessage(replaced);
            target.sendMessage(component);
        }
    }

    private void extractLangFiles(@NotNull File outDir) {
        try {
            for(String file : JarReader.extractJarFiles("langs/")) {
                if(!file.endsWith(".yml")) continue;

                String fileName = file.substring(file.lastIndexOf('/') + 1);
                File outFile = new File(outDir, fileName);
                QuickPartyLogger.info("Extract lang-file to: '" + outFile + "'");
                try(var inputStream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(file))) {
                    java.nio.file.Files.copy(
                        inputStream,
                        outFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                }
            }
        } catch(Exception e) {
            QuickPartyLogger.error("Could not walk over internal lang files", e);
        }
    }

    public String getPrefix(boolean isLeader, boolean isSelf, boolean isOnline) {
        return config.getPlaceholders().prefix().get(isLeader, isSelf, isOnline);
    }
    public String getSuffix(boolean isLeader, boolean isSelf, boolean isOnline) {
        return config.getPlaceholders().suffix().get(isLeader, isSelf, isOnline);
    }

}
