package su.nightexpress.excellentclaims.api.session;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.excellentclaims.api.claim.Claim;
import su.nightexpress.nightcore.bridge.key.AdaptedKey;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;

@NullMarked
public interface PlayerSession {

    UUID getPlayerId();

    @Nullable
    AdaptedKey getWorldKey();

    @Nullable
    BlockPos getBlockPos();

    @Nullable
    Claim getCurrentClaim();

    void updatePosition(AdaptedKey worldKey, BlockPos blockPos, @Nullable Claim currentClaim);
}