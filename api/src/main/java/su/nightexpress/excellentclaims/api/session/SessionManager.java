package su.nightexpress.excellentclaims.api.session;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentclaims.api.claim.Claim;

@NullMarked
public interface SessionManager {

    PlayerSession getOrCreateSession(Player player);

    PlayerSession getOrCreateSession(UUID playerId);

    @Nullable
    PlayerSession getSession(Player player);

    @Nullable
    PlayerSession getSession(UUID playerId);

    @Nullable
    Claim getCurrentClaim(Player player);

    void updateSession(Player player, Location toLocation, boolean force);

    void removeSession(Player player);

    void removeSession(UUID playerId);

    void clear();

    void addClaimChangeListener(SessionClaimChangeListener listener);
}