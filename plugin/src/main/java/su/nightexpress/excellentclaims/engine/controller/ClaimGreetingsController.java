package su.nightexpress.excellentclaims.engine.controller;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentclaims.api.ClaimPlugin;
import su.nightexpress.excellentclaims.api.ClaimsPlaceholders;
import su.nightexpress.excellentclaims.api.claim.Claim;
import su.nightexpress.excellentclaims.api.core.AbstractController;
import su.nightexpress.excellentclaims.api.session.PlayerSession;
import su.nightexpress.excellentclaims.api.session.SessionManager;
import su.nightexpress.excellentclaims.core.Lang;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;

@NullMarked
public class ClaimGreetingsController extends AbstractController {

    public ClaimGreetingsController(ClaimPlugin plugin, SessionManager sessionManager) {
        super(plugin);
        sessionManager.addClaimChangeListener(this::onClaimChange);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onShutdown() {
    }

    @Override
    protected void onReload() {
    }

    private void onClaimChange(Player player, PlayerSession session, @Nullable Claim oldClaim, @Nullable Claim newClaim) {
        if (oldClaim == newClaim || newClaim == null || newClaim.isBackgroundClaim()) return;

        Lang.GREETING_CLAIM.message().sendWith(player, builder -> builder
                .with(CommonPlaceholders.GENERIC_NAME, newClaim::getName)
                .with(ClaimsPlaceholders.GENERIC_DESCRIPTION, () -> String.join("\n", newClaim.getDescription()))
        );
    }
}