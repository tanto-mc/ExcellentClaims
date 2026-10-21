package su.nightexpress.excellentclaims.rules.impl.player;

import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

import su.nightexpress.excellentclaims.api.claim.Claim;
import su.nightexpress.excellentclaims.api.rule.RuleBehavior;
import su.nightexpress.excellentclaims.api.rule.RuleCategory;
import su.nightexpress.excellentclaims.api.rule.RuleDefinition;
import su.nightexpress.excellentclaims.api.rule.RuleResult;
import su.nightexpress.excellentclaims.api.session.SessionManager;
import su.nightexpress.excellentclaims.rules.evaluation.context.player.PlayerFlyContext;
import su.nightexpress.excellentclaims.rules.spec.SimpleSpec;
import su.nightexpress.excellentclaims.rules.type.RuleTypes;

@NullMarked
public class PlayerFlyRule extends SimpleSpec<PlayerFlyContext, Boolean> {

    private final SessionManager sessionManager;

    public PlayerFlyRule(SessionManager sessionManager) {
        super(PlayerFlyContext.class, RuleTypes.BOOLEAN, RuleCategory.PLAYER);
        this.sessionManager = sessionManager;
    }

    @Override
    public RuleBehavior<PlayerFlyContext, Boolean> createBehavior() {
        return this.behaviorBuilder()
                .process((event, registry, context) -> {
                    Claim claim = this.sessionManager.getCurrentClaim(event.actor());
                    if (claim == null) return RuleResult.pass();

                    boolean allow = context.resolveValue(claim).orElse(this.getDefaultValue());
                    return RuleResult.of(allow);
                })
                .build();
    }

    @Override
    public RuleDefinition getDefaultDefinition() {
        return RuleDefinition.builder("Player Fly")
                .description("Controls whether players can fly in this claim.")
                .icon(Material.FEATHER)
                .build();
    }

    @Override
    public Boolean getDefaultValue() {
        return false;
    }
}