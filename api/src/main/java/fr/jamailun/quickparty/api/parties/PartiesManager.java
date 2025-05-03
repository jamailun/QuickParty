package fr.jamailun.quickparty.api.parties;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public interface PartiesManager {

    @NotNull @UnmodifiableView
    Collection<Party> getParties();

    default @Nullable Party getPlayerParty(@NotNull Player player) {
        return getPlayerParty(player.getUniqueId());
    }

    @Nullable Party getPlayerParty(@NotNull UUID playerUuid);

    @NotNull PartyInvitationResult invitePlayer(@NotNull Player playerFrom, @NotNull Player playerTo);

    boolean hasInvitation(@NotNull OfflinePlayer player);
    @Nullable PartyInvitation getInvitationFor(@NotNull OfflinePlayer player);

}
