package su.nightexpress.excellentclaims.rules.evaluation.context.player;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import su.nightexpress.excellentclaims.api.rule.context.ActionContext;
import su.nightexpress.excellentclaims.api.rule.context.ActorContext;

@NullMarked
public record PlayerGodContext(Player actor) implements ActionContext, ActorContext {

}