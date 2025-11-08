package fr.jamailun.quickparty.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import fr.jamailun.quickparty.QuickPartyLogger;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import fr.jamailun.quickparty.configuration.parts.TeleportModeSection;
import fr.jamailun.quickparty.utils.JarReader;
import fr.jamailun.quickparty.utils.StringUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

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
    private Set<TeleportMode> cachedTeleportMode;

    @Getter public static boolean isDebug = false;

    // Translations
    private final Map<String, String> i18n = new HashMap<>();
    private final List<String> messageInvitation = new ArrayList<>();
    private final List<String> messageTp = new ArrayList<>();
    private final List<String> messageTpAll = new ArrayList<>();

    public QuickPartyConfig(@NotNull Plugin plugin) {
        dataFolder = plugin.getDataFolder();
        file = new File(dataFolder, "config.yml");

        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        store = new YamlConfigurationStore<>(MainConfiguration.class, properties);

        instance = this;

        reload();
    }

    /**
     * Reload config entries and I18n.
     */
    public void reload() {
        // Clear
        datetimeFormat = null;
        i18n.clear();
        messageInvitation.clear();
        messageTp.clear();
        messageTpAll.clear();
        cachedTeleportMode = null;

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
                messageInvitation.addAll(langConfig.getStringList(key));
            } else if("players.teleport.messages.tp".equalsIgnoreCase(key)) {
                messageTp.addAll(langConfig.getStringList(key));
            } else if("players.teleport.messages.tpall".equalsIgnoreCase(key)) {
                messageTpAll.addAll(langConfig.getStringList(key));
            } else {
                i18n.put(key, langConfig.getString(key));
            }
        }
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

    public void sendInvitationMessageTo(@NotNull Player inviter, @NotNull CommandSender target) {
        sendComplexMessage(target, messageInvitation, "", l -> l.replace("%player", inviter.getName()));
    }

    /**
     * Send a TP request.
     * @param waiting player waiting for the other to accept/refuse the TP request.
     * @param awaited player that need to accept to.
     * @param mode teleport mode.
     */
    public void sendTpRequest(@NotNull Player waiting, @NotNull Player awaited, @NotNull TeleportMode mode) {
        var message = (mode == TeleportMode.ALL_TO_LEADER) ? messageTpAll : messageTp;
        sendComplexMessage(awaited, message, " " + waiting.getName(), l -> l.replace("%player", waiting.getName()));
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

    public @NotNull TeleportModeSection getTeleportRules(@NotNull TeleportMode mode) {
        return config.getTeleportation().completeFor(mode);
    }

    public @NotNull Set<TeleportMode> getEnabledTeleportModes() {
        if(cachedTeleportMode == null) {
            cachedTeleportMode = config.getTeleportation().getEnabledModes();
        }
        return cachedTeleportMode;
    }

    public @NotNull Duration getTeleportRequestExpiration() {
        double seconds = config.getTeleportation().requestExpirationSeconds();
        if(seconds <= 0)
            return Duration.ofDays(7);
        return Duration.ofMillis((long) (seconds * 1000));
    }

    private static void sendComplexMessage(@NotNull CommandSender target, @NotNull List<String> message, @NotNull String arg, @NotNull Function<String, String> messageTransformation) {
        for(String line : message) {
            // Replace
            String replaced =  messageTransformation.apply(line);

            // Transform
            BaseComponent[] components = prepareComplexMessage(replaced, arg);
            target.spigot().sendMessage(components);
        }
    }

    private static @NotNull BaseComponent @NotNull [] prepareComplexMessage(@NotNull String raw, @NotNull String arg) {
        List<String> parts = StringUtils.splitWithDelimiters(raw, "%CMD_ACCEPT", "%CMD_REFUSE");
        List<BaseComponent> components = new ArrayList<>();
        for(String part : parts) {
            TextComponent text;
            if("%CMD_ACCEPT".equals(part)) {
                text = new TextComponent("§a/p accept");
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p accept" + arg));
            } else if("%CMD_REFUSE".equals(part)) {
                text = new TextComponent("§c/p refuse");
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p refuse" + arg));
            } else {
                text = new TextComponent(StringUtils.parseString(part));
            }
            components.add(text);
        }
        return components.toArray(new BaseComponent[0]);
    }

}
