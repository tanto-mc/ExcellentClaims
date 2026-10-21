package su.nightexpress.excellentclaims.core.session;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentclaims.api.claim.Claim;
import su.nightexpress.excellentclaims.api.session.PlayerSession;
import su.nightexpress.nightcore.bridge.key.AdaptedKey;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

@NullMarked
public class DefaultPlayerSession implements PlayerSession {

    private final UUID playerId;

    @Nullable
    private AdaptedKey worldKey;
    @Nullable
    private BlockPos   blockPos;
    @Nullable
    private Claim      currentClaim;

    public DefaultPlayerSession(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public UUID getPlayerId() {
        return this.playerId;
    }

    @Override
    public @Nullable AdaptedKey getWorldKey() {
        return this.worldKey;
    }

    @Override
    public @Nullable BlockPos getBlockPos() {
        return this.blockPos;
    }

    @Override
    public @Nullable Claim getCurrentClaim() {
        if (this.currentClaim != null && this.currentClaim.isDisabled()) {
            this.currentClaim = null;
        }
        return this.currentClaim;
    }

    @Override
    public void updatePosition(AdaptedKey worldKey, BlockPos blockPos, @Nullable Claim currentClaim) {
        this.worldKey = worldKey;
        this.blockPos = blockPos;
        this.currentClaim = currentClaim;
    }
}