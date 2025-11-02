package fr.jamailun.quickparty.api.events;

import fr.jamailun.quickparty.api.parties.Party;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class PartyEvent extends Event {

    /**
     * The {@link Party} implicated in this event.
     */
    public abstract @NotNull Party getParty();

}
