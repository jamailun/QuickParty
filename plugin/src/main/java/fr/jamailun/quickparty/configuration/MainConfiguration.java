package fr.jamailun.quickparty.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import fr.jamailun.quickparty.QuickPartyLogger;
import fr.jamailun.quickparty.configuration.parts.TeleportationSection;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Version {@link MainConfiguration#LATEST_VERSION} of configuration file.
 */
@Getter
@Configuration
public class MainConfiguration {

    /**
     * Current target version.
     */
    private static final String LATEST_VERSION = "2.0";

    @Comment("If true, 'debug' log will be printed into the console.")
    private boolean debug = false;

    @Comment({"","Language to use. The file must exist in the lang/ folder. You don't need to specify the file extension."})
    private String lang;

    @Comment({"","Default values of the parties."})
    private PartySettings parties;

    @Comment({"","Formats configuration"})
    private FormatSettings format;

    @Comment({"", "Teleport rules and costs"})
    private TeleportationSection teleportation;

    public record PartySettings(
        @Comment("If the party members can deal damage between them.")
        boolean friendlyFire,
        @Comment("Maximum size of parties.")
        int maxSize
    ) {
        PartySettings asValid() {
            return new PartySettings(friendlyFire, maxSize < 2 ? 2 : maxSize);
        }
    }
    public record FormatSettings(
        @Comment("Output date format.")
        String dateTime
    ) {
        FormatSettings asValid() {
            try {
                DateTimeFormatter.ofPattern(dateTime);
                return this;
            } catch(Exception ignored) {
                return new FormatSettings("HH:mm:ss");
            }
        }
    }

    @Comment({"", "Placeholder customization.", "Used by the %qpa_party_nice_member_<INDEX>% placeholder."})
    private PlaceholdersEntry placeholders;

    public record PlaceholdersEntry(PrefixEntry prefix, PrefixEntry suffix) {
        PlaceholdersEntry asValid() {
            return new PlaceholdersEntry(
                prefix == null ? new PrefixEntry(new OnlineEntry("&6★ ", "&6★ &7", "&2★ &6"), new OnlineEntry("&a▪ ", "&a▫ &7", "&2b &a")) : prefix.asValid(),
                suffix == null ? new PrefixEntry(new OnlineEntry("","", ""), new OnlineEntry("", "", "")) : suffix.asValid()
            );
        }
    }
    public record PrefixEntry(OnlineEntry leader, OnlineEntry member) {
        PrefixEntry asValid() {
            return new PrefixEntry(
                leader == null ? new OnlineEntry("", "", "") : leader.asValid(),
                member == null ? new OnlineEntry("", "", "") : member.asValid()
            );
        }
        public String get(boolean isLeader, boolean isSelf, boolean isOnline) {
            OnlineEntry entry = isLeader ? leader : member;
            return entry.get(isSelf, isOnline);
        }
    }
    private record OnlineEntry(String online, String offline, String self) {
        OnlineEntry asValid() {return new OnlineEntry(Objects.requireNonNullElse(online, ""), Objects.requireNonNullElse(offline, ""), Objects.requireNonNullElse(self, ""));}
        String get(boolean isSelf, boolean isOnline) {
            return isSelf ? self : (isOnline ? online : offline);
        }
    }

    @Comment({"","Dont change this value manually."})
    @Setter private String version = LATEST_VERSION;

    public void checkValidity() {
        if(LATEST_VERSION.compareTo(Objects.requireNonNullElse(version, "0")) > 0) {
            QuickPartyLogger.warn("Configuration upgrade from " + version + " to " + LATEST_VERSION + ".");
            version = LATEST_VERSION;
        }

        // Lang
        if(lang == null || lang.isEmpty())
            lang = "en_US";

        // Parties
        if(parties == null) parties = new PartySettings(true, 4);
        parties = parties.asValid();

        // Format
        if(format == null) format = new FormatSettings(null);
        format = format.asValid();

        // Teleportation
        if(teleportation == null) teleportation = new TeleportationSection(null,null, null);
        teleportation = teleportation.asValid();

        // Placeholders
        if(placeholders == null) placeholders = new PlaceholdersEntry(null, null);
        placeholders = placeholders.asValid();
    }

}
