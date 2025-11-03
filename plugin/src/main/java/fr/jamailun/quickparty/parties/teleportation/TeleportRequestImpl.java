package fr.jamailun.quickparty.parties.teleportation;

import com.google.common.base.Preconditions;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyMember;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportRequest;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportState;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import lombok.Getter;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Implementation for a party TP request.
 */
@Getter
public class TeleportRequestImpl implements TeleportRequest {

    private static final String ERROR = "&c";
    private static final String SUCCESS = "&a";

    private final PartyMember player;
    private final PartyMember destination;
    private final TeleportMode mode;

    private final LocalDateTime date;
    private final LocalDateTime expirationDate;

    private TeleportState state = TeleportState.PENDING;

    public TeleportRequestImpl(@NotNull PartyMember player, @NotNull PartyMember destination, @NotNull TeleportMode mode) {
        this.player = player;
        this.destination = destination;
        this.mode = mode;
        // Dates
        date = LocalDateTime.now();
        expirationDate = date.plus(QuickPartyConfig.getInstance().getTeleportRequestExpiration());
    }

    @Override
    public @NotNull Party getParty() {
        return player.getParty();
    }

    /**
     * To call after the expiration delay.
     */
    public void expired() {
        if(!isPending()) return;
        state = TeleportState.EXPIRED;

        if(isTpAll()) {
            playerI18n("players.teleport.expired-group", ERROR);
            destinationI18n("players.teleport.expired-tpall", ERROR);
            playSound(player, Sound.ENTITY_ALLAY_DEATH);
            playSound(destination, Sound.BLOCK_CHORUS_FLOWER_DEATH);
        } else {
            playerI18n("players.teleport.expired", ERROR);
            playSound(player, Sound.ENTITY_ALLAY_DEATH);
        }
    }

    public void abandon() {
        if(!isPending()) return;
        state = TeleportState.ABANDONED;

        if(isTpAll()) {
            playerI18n("players.teleport.cancelled-group", ERROR);
            destinationI18n("players.teleport.abandoned-tpall", ERROR);
            playSound(player, Sound.ENTITY_ALLAY_DEATH);
        } else {
            playerI18n("players.teleport.abandoned", ERROR);
            playSound(destination, Sound.BLOCK_CHORUS_FLOWER_DEATH);
        }
    }

    @Override
    public void accept() {
        Preconditions.checkState(isPending(), "Cannot accept if state not PENDING. Current state = " + state + ".");

        if(!canTeleport()) {
            state = TeleportState.ACCEPTED_ERROR;
            playerI18n("players.teleport.impossible", ERROR);
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        state = TeleportState.ACCEPTED_SUCCESS;
        //TODO teleport cooldown and stuff

        player.getOnlinePlayer().teleport(destination.getOnlinePlayer());
        playSound(player, Sound.ENTITY_PLAYER_TELEPORT);
        playSound(destination, Sound.BLOCK_NOTE_BLOCK_CHIME);
        playerI18n("players.teleport.success", SUCCESS);
        if(isTpAll()) {
            destinationI18n("players.teleport.success-tpall", SUCCESS);
        } else {
            destinationI18n("players.teleport.success-other", SUCCESS);
        }
    }

    @Override
    public void cancel() {
        Preconditions.checkState(isPending(), "Cannot cancel if state not PENDING. Current state = " + state + ".");
        state = TeleportState.EXPIRED;

        player.sendMessage(QuickPartyConfig.getI18n("players.teleport.cancelled"));
        destination.sendMessage(QuickPartyConfig.getI18n("players.teleport.other-cancelled").replace("%player", player.getName()));
    }

    private boolean canTeleport() {
        return destination.isOnline() && destination.getOnlinePlayer().isValid()
                && player.isOnline() && player.getOnlinePlayer().isValid();
    }

    private boolean isTpAll() {
        return mode == TeleportMode.ALL_TO_LEADER;
    }

    private void playerI18n(@NotNull String key, @NotNull String color) {
        String i18n = QuickPartyConfig.getI18n(key);
        String msg = color + i18n.replace("%player", destination.getName()).replace("&r", color);
        player.sendMessage(msg);
    }
    private void destinationI18n(@NotNull String key, @NotNull String color) {
        String i18n = QuickPartyConfig.getI18n(key);
        String msg = color + i18n.replace("%player", player.getName()).replace("&r", color);
        destination.sendMessage(msg);
    }
    private void playSound(@NotNull PartyMember member, @NotNull Sound sound) {
        member.playSound(sound, 1.2f);
    }

    public boolean isPending() {
        return state == TeleportState.PENDING;
    }
}
