package su.nightexpress.excellentclaims.api.session;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentclaims.api.claim.Claim;

@NullMarked
@FunctionalInterface
public interface SessionClaimChangeListener {

    void onClaimChange(Player player, PlayerSession session, @Nullable Claim oldClaim, @Nullable Claim newClaim);
}