package su.nightexpress.excellentclaims.core.session;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentclaims.api.ClaimRegistry;
import su.nightexpress.excellentclaims.api.claim.Claim;
import su.nightexpress.excellentclaims.api.session.PlayerSession;
import su.nightexpress.excellentclaims.api.session.SessionClaimChangeListener;
import su.nightexpress.excellentclaims.api.session.SessionManager;
import su.nightexpress.nightcore.bridge.BukkitKeys;
import su.nightexpress.nightcore.bridge.key.AdaptedKey;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

@NullMarked
public class DefaultSessionManager implements SessionManager {

    private final ClaimRegistry                    claimRegistry;
    private final Map<UUID, PlayerSession>         sessions;
    private final List<SessionClaimChangeListener> listeners;

    public DefaultSessionManager(ClaimRegistry claimRegistry) {
        this.claimRegistry = claimRegistry;
        this.sessions = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public PlayerSession getOrCreateSession(Player player) {
        return this.getOrCreateSession(player.getUniqueId());
    }

    @Override
    public PlayerSession getOrCreateSession(UUID playerId) {
        return this.sessions.computeIfAbsent(playerId, DefaultPlayerSession::new);
    }

    @Override
    public @Nullable PlayerSession getSession(Player player) {
        return this.getSession(player.getUniqueId());
    }

    @Override
    public @Nullable PlayerSession getSession(UUID playerId) {
        return this.sessions.get(playerId);
    }

    @Override
    public @Nullable Claim getCurrentClaim(Player player) {
        PlayerSession session = this.getSession(player);
        if (session != null && session.getBlockPos() != null) {
            return session.getCurrentClaim();
        }
        return this.claimRegistry.getPrioritizedClaim(player);
    }

    @Override
    public void updateSession(Player player, Location toLocation, boolean force) {
        World world = toLocation.getWorld();
        if (world == null) return;

        AdaptedKey worldKey = BukkitKeys.getKey(world);
        BlockPos blockPos = BlockPos.from(toLocation);

        PlayerSession session = this.getOrCreateSession(player);

        if (!force
                && worldKey.equals(session.getWorldKey())
                && blockPos.equals(session.getBlockPos())) {
            return;
        }

        Claim oldClaim = session.getCurrentClaim();
        Claim newClaim = this.claimRegistry.getPrioritizedClaim(worldKey, blockPos);

        if (oldClaim != newClaim) {
            System.out.println("[Debug Manager] Claim changed! Old: " + (oldClaim != null ? oldClaim.getId() : "null") + " -> New: " + (newClaim != null ? newClaim.getId() : "null"));
        }

        session.updatePosition(worldKey, blockPos, newClaim);

        if (force || !Objects.equals(oldClaim, newClaim)) {
            for (SessionClaimChangeListener listener : this.listeners) {
                listener.onClaimChange(player, session, oldClaim, newClaim);
            }
        }
    }

    @Override
    public void removeSession(Player player) {
        this.removeSession(player.getUniqueId());
    }

    @Override
    public void removeSession(UUID playerId) {
        this.sessions.remove(playerId);
    }

    @Override
    public void clear() {
        this.sessions.clear();
    }

    @Override
    public void addClaimChangeListener(SessionClaimChangeListener listener) {
        this.listeners.add(listener);
    }
}