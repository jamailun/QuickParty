package fr.jamailun.quickparty.parties;

import fr.jamailun.quickparty.QuickPartyLogger;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyMember;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import fr.jamailun.quickparty.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.*;

@Getter @Setter
public class PartyMemberImpl implements PartyMember {

    private final Party party;
    private OfflinePlayer offlinePlayer;
    private boolean isPartyLeader;

    PartyMemberImpl(@NotNull Party party, @NotNull OfflinePlayer player) {
        this.party = party;
        this.offlinePlayer = player;
    }

    @Override
    public boolean isOnline() {
        return offlinePlayer.isOnline();
    }

    @Override
    public Player getOnlinePlayer() {
        return offlinePlayer.getPlayer();
    }

    @Override
    public @NotNull UUID getUUID() {
        return offlinePlayer.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return Objects.requireNonNullElse(offlinePlayer.getName(), offlinePlayer.getUniqueId().toString());
    }

    @Override
    public void refreshOnline() {
        Player onlinePlayer = Bukkit.getPlayer(getUUID());
        if(onlinePlayer == null) {
            QuickPartyLogger.warn("A call to PartyMemberImpl#refreshOnline has been made without the player being actually online. UUID = '"+getUUID()+"'.");
        } else {
            offlinePlayer = onlinePlayer;
            QuickPartyLogger.debug("Player " + onlinePlayer.getName() + " has reconnected.");
        }
    }

    @Override
    public void sendMessage(@NotNull String message) {
        Optional.ofNullable(getOnlinePlayer())
                .ifPresent(p -> p.sendMessage(StringUtils.parseString(message)));
    }

    @Override
    public void playSound(@NotNull Sound sound, float pitch) {
        Optional.ofNullable(getOnlinePlayer())
                .ifPresent(p -> p.playSound(p, sound, 1f, pitch));
    }

    @Override
    public void sendTeleportRequest(@NotNull Player destination, @NotNull TeleportMode mode) {
        if(isOnline())
            party.newTeleportRequest(getOnlinePlayer(), destination, mode);
    }
}
