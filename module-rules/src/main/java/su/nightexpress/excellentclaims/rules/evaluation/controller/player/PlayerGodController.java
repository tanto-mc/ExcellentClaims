package su.nightexpress.excellentclaims.rules.evaluation.controller.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import su.nightexpress.excellentclaims.rules.evaluation.context.player.PlayerGodContext;
import su.nightexpress.excellentclaims.rules.evaluation.controller.AbstractEventController;

@NullMarked
public class PlayerGodController extends AbstractEventController {

    private final ClaimPlugin plugin;
    private final ClaimPermissionAPI permissions;

    public PlayerGodController(ClaimPlugin plugin,
                               EvaluatorEngine engine,
                               SessionManager sessionManager,
                               ClaimPermissionAPI permissions) {
        super(engine);
        this.plugin = plugin;
        this.permissions = permissions;

        this.plugin.getLogger().info("[Debug God] >>> CONSTRUCTOR CALLED! Registering claim change listener...");
        sessionManager.addClaimChangeListener(this::onClaimChange);
    }

    private void onClaimChange(Player player, PlayerSession session, @Nullable Claim oldClaim, @Nullable Claim newClaim) {
        this.plugin.getLogger().info("[Debug God] >>> ON_CLAIM_CHANGE FIRED for " + player.getName() + "! Old: " + (oldClaim != null ? oldClaim.getId() : "Wild") + ", New: " + (newClaim != null ? newClaim.getId() : "Wild"));
        this.applyGodRule(player);
    }

    public void applyGodRule(Player player) {
        if (!player.isOnline()) return;

        EventState godState = this.engine.evaluate(new PlayerGodContext(player)).state();
        this.plugin.getLogger().info("[Debug God] Player " + player.getName() + " evaluated god state: " + godState);

        boolean hasBypass = this.permissions.hasBypass(player);

        if (godState == EventState.DENY && !hasBypass) {
            if (player.isInvulnerable()) {
                player.setInvulnerable(false);
                this.plugin.getLogger().info("[Debug God] Revoked god mode for " + player.getName() + " due to DENY rule.");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
        }
    }
}