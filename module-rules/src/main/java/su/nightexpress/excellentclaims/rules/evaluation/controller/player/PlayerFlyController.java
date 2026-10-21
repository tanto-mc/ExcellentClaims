package su.nightexpress.excellentclaims.rules.evaluation.controller.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentclaims.api.ClaimPlugin;
import su.nightexpress.excellentclaims.api.claim.Claim;
import su.nightexpress.excellentclaims.api.claim.ClaimPermissionAPI;
import su.nightexpress.excellentclaims.api.rule.EventState;
import su.nightexpress.excellentclaims.api.session.PlayerSession;
import su.nightexpress.excellentclaims.api.session.SessionManager;
import su.nightexpress.excellentclaims.rules.evaluation.EvaluatorEngine;
import su.nightexpress.excellentclaims.rules.evaluation.context.player.PlayerFlyContext;
import su.nightexpress.excellentclaims.rules.evaluation.controller.AbstractEventController;

@NullMarked
public class PlayerFlyController extends AbstractEventController {

    private final ClaimPlugin        plugin;
    private final ClaimPermissionAPI permissions;
    private final Map<UUID, Boolean> originalFlyMap;

    public PlayerFlyController(ClaimPlugin plugin,
                               EvaluatorEngine engine,
                               SessionManager sessionManager,
                               ClaimPermissionAPI permissions) {
        super(engine);
        this.plugin = plugin;
        this.permissions = permissions;
        this.originalFlyMap = new ConcurrentHashMap<>();

        sessionManager.addClaimChangeListener(this::onClaimChange);
    }

    private void onClaimChange(Player player, PlayerSession session, @Nullable Claim oldClaim, @Nullable Claim newClaim) {
        this.applyFlyRule(player);
    }

    public void applyFlyRule(Player player) {
        if (!player.isOnline()) return;

        boolean nativeFlight = this.isNativeFlightGameMode(player.getGameMode());
        if (this.permissions.hasBypass(player) || nativeFlight) {
            this.restoreFly(player);
            return;
        }

        EventState flyState = this.engine.evaluate(new PlayerFlyContext(player)).state();
        this.plugin.getLogger().info("[Debug Fly] Player " + player.getName() + " evaluated fly state: " + flyState);

        if (flyState == EventState.PASS) {
            this.restoreFly(player);
            return;
        }

        boolean allowFly = flyState == EventState.ALLOW;
        this.originalFlyMap.putIfAbsent(player.getUniqueId(), player.getAllowFlight());

        if (player.getAllowFlight() != allowFly) {
            player.setAllowFlight(allowFly);
            if (!allowFly && player.isFlying()) {
                player.setFlying(false);
                player.setFallDistance(0F);
            }
        }
    }

    private void restoreFly(Player player) {
        Boolean originalFly = this.originalFlyMap.remove(player.getUniqueId());
        if (originalFly == null) return;

        boolean canFly = originalFly || this.isNativeFlightGameMode(player.getGameMode());
        player.setAllowFlight(canFly);
        if (!canFly && player.isFlying()) {
            player.setFlying(false);
            player.setFallDistance(0F);
        }
    }

    private boolean isNativeFlightGameMode(GameMode mode) {
        return mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (!player.isOnline()) return;
            if (this.originalFlyMap.containsKey(player.getUniqueId())) {
                this.originalFlyMap.put(player.getUniqueId(), player.getAllowFlight());
            }
            this.applyFlyRule(player);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        this.originalFlyMap.remove(event.getPlayer().getUniqueId());
    }
}