package fr.jamailun.quickparty.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

/**
 * Version {@code 1.0} of configuration file.
 */
@Getter
@Configuration
public class MainConfiguration {

    @Comment("Dont change this value manually.")
    @Getter private String version = "1.0";

    @Comment("If true, 'debug' log will be printed into the console.")
    @Getter private boolean debug = false;

    @Comment("Default values of the parties.")
    private PartySettings parties = new PartySettings(true, 4);

    @Comment("Formats configuration")
    private FormatSettings format = new FormatSettings("HH:mm:ss");

    public record PartySettings(
        @Comment("If the party members can deal damage between them.")
        boolean friendlyFire,
        @Comment("Maximum size of parties.")
        int maxSize
    ) {}
    public record FormatSettings(
        @Comment("Output date format.")
        String dateTime
    ) {}

}
