package fr.jamailun.quickparty.parties.teleportation;

import com.google.common.base.Preconditions;
import fr.jamailun.quickparty.QuickPartyScheduler;
import fr.jamailun.quickparty.api.parties.Party;
import fr.jamailun.quickparty.api.parties.PartyMember;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportMode;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportRequest;
import fr.jamailun.quickparty.api.parties.teleportation.TeleportState;
import fr.jamailun.quickparty.configuration.QuickPartyConfig;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementation for a party TP request.
 */
@Getter
public class TeleportRequestImpl implements TeleportRequest {

    private static final String ERROR = "&c";
    private static final String SUCCESS = "&a";
    private static final DecimalFormat FORMAT = new DecimalFormat("#.#");

    private final PartyMember awaited;
    private final PartyMember waiting;
    private final TeleportMode mode;
    private final Consumer<TeleportRequest> removeCallback;
    private final String delay;

    private final LocalDateTime date;
    private final LocalDateTime expirationDate;

    private TeleportState state = TeleportState.PENDING;

    /**
     * New instance.
     * @param awaited player that need to accept the request.
     * @param waiting player waiting for the other one to accept the request.
     * @param mode teleport mode.
     * @param removeCallback callback to call whe removal is required.
     */
    public TeleportRequestImpl(@NotNull PartyMember awaited, @NotNull PartyMember waiting, @NotNull TeleportMode mode, @NotNull Consumer<TeleportRequest> removeCallback) {
        this.awaited = awaited;
        this.waiting = waiting;
        this.mode = mode;
        this.removeCallback = removeCallback;
        // Dates
        date = LocalDateTime.now();
        expirationDate = date.plus(QuickPartyConfig.getInstance().getTeleportRequestExpiration());

        var rules = QuickPartyConfig.getInstance().getTeleportRules(mode);
        Double rawDelay = rules.teleportWaitSecs();
        delay = rawDelay == null ? "0" : FORMAT.format(rawDelay);
        if( ! rules.doesNeedConfirmation()) {
            instantTeleport();
        }
    }

    private void instantTeleport() {
        actuallyTeleport();
        QuickPartyScheduler.runLater(() -> removeCallback.accept(this), Duration.ofMillis(100));
    }

    @Override
    public @NotNull Party getParty() {
        return getPlayerToAccept().getParty();
    }

    @Override
    public @NotNull PartyMember getPlayerToAccept() {
        return awaited;
    }

    @Override
    public @NotNull PartyMember getPlayerWaiting() {
        return waiting;
    }

    /**
     * To call after the expiration delay.
     */
    public void expired() {
        if(!isPending()) return;
        state = TeleportState.EXPIRED;

        // Action
        if(isTpAll()) {
            waitingI18n("players.teleport.expired-group", ERROR);
            awaitedI18n("players.teleport.expired-tpall", ERROR);
        } else {
            waitingI18n("players.teleport.expired", ERROR);
            awaitedI18n("players.teleport.expired-other", ERROR);
        }
        playSound(waiting, Sound.ENTITY_ALLAY_DEATH);
        playSound(awaited, Sound.BLOCK_CHORUS_FLOWER_DEATH);

        // Remove
        removeCallback.accept(this);
    }

    @Override
    public void accept() {
        Preconditions.checkState(isPending(), "Cannot accept if state not PENDING. Current state = " + state + ".");

        if(!canTeleport()) {
            state = TeleportState.ACCEPTED_ERROR;
            waitingI18n("players.teleport.impossible", ERROR);
            playSound(getPlayerToTeleport(), Sound.ENTITY_VILLAGER_NO);
            return;
        }

        state = TeleportState.ACCEPTED_SUCCESS;

        // Si on a un délai, on envoie un message de confirmation avant de vraiment téléporter.
        double toWait = Objects.requireNonNullElse(QuickPartyConfig.getInstance().getTeleportRules(mode).teleportWaitSecs(), 0d);
        if(toWait > 0) {
            if(isTpAll()) {
                waitingI18n("players.teleport.accepted-tpall", SUCCESS);
                awaitedI18n("players.teleport.accepted", SUCCESS);
            } else {
                waitingI18n("players.teleport.accepted", SUCCESS);
                awaitedI18n("players.teleport.accepted-other", SUCCESS);
            }
            playSound(getPlayerToTeleport(), Sound.BLOCK_NOTE_BLOCK_BIT);
            playSound(getDestination(), Sound.BLOCK_NOTE_BLOCK_BIT);

            int ticks = (int) (toWait * 1000 / 50);
            Player player = getPlayerToTeleport().getOnlinePlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 10));
            QuickPartyScheduler.runLater(this::actuallyTeleport, Duration.ofMillis((long)(toWait * 1000)));
        }
        // Sinon, on téléporte directement.
        else {
            actuallyTeleport();
        }

        removeCallback.accept(this);
    }

    private void actuallyTeleport() {
        // Teleport + effects
        getPlayerToTeleport().getOnlinePlayer().teleport(getDestination().getOnlinePlayer());
        playSound(getPlayerToTeleport(), Sound.ENTITY_PLAYER_TELEPORT);
        playSound(getDestination(), Sound.BLOCK_NOTE_BLOCK_CHIME);

        // Messages
        if(isTpAll()) {
            awaitedI18n("players.teleport.success", SUCCESS);
            waitingI18n("players.teleport.success-tpall", SUCCESS);
        } else {
            awaitedI18n("players.teleport.success-other", SUCCESS);
            waitingI18n("players.teleport.success", SUCCESS);
        }
    }

    @Override
    public void cancel() {
        Preconditions.checkState(isPending(), "Cannot cancel if state not PENDING. Current state = " + state + ".");
        state = TeleportState.EXPIRED;

        if(isTpAll()) {
            waitingI18n("players.teleport.cancelled-tpall", ERROR);
            awaitedI18n("players.teleport.cancelled-group", ERROR);
        } else {
            waitingI18n("players.teleport.cancelled", ERROR);
            awaitedI18n("players.teleport.cancelled-other", ERROR);
        }
    }

    private boolean canTeleport() {
        return getDestination().isOnline() && getDestination().getOnlinePlayer().isValid()
                && getPlayerToTeleport().isOnline() && getPlayerToTeleport().getOnlinePlayer().isValid();
    }

    private boolean isTpAll() {
        return mode == TeleportMode.ALL_TO_LEADER;
    }

    private void waitingI18n(@NotNull String key, @NotNull String color) {
        String i18n = QuickPartyConfig.getI18n(key);
        String msg = color + i18n.replace("%player", awaited.getName()).replace("&r", color).replace("%delay", delay);
        waiting.sendMessage(msg);
    }
    private void awaitedI18n(@NotNull String key, @NotNull String color) {
        String i18n = QuickPartyConfig.getI18n(key);
        String msg = color + i18n.replace("%player", waiting.getName()).replace("&r", color).replace("%delay", delay);
        awaited.sendMessage(msg);
    }
    private void playSound(@NotNull PartyMember member, @NotNull Sound sound) {
        member.playSound(sound, 1.2f);
    }

    public boolean isPending() {
        return state == TeleportState.PENDING;
    }
}
