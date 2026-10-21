package su.nightexpress.excellentclaims.engine.controller;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.jspecify.annotations.NullMarked;
import su.nightexpress.excellentclaims.api.ClaimPlugin;
import su.nightexpress.excellentclaims.api.core.AbstractController;
import su.nightexpress.excellentclaims.api.session.SessionManager;

@NullMarked
public class PlayerSessionController extends AbstractController {

    private final SessionManager sessionManager;

    public PlayerSessionController(ClaimPlugin plugin, SessionManager sessionManager) {
        super(plugin);
        this.sessionManager = sessionManager;
    }

    @Override
    protected void onStart() {
        this.refreshOnlinePlayers();
        this.addTickTask(this::refreshOnlinePlayers, 20L);
    }

    @Override
    protected void onShutdown() {
        this.sessionManager.clear();
    }

    @Override
    protected void onReload() {
        this.stopTasks();
        this.onStart();
    }

    private void refreshOnlinePlayers() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            Location location = player.getLocation();
            this.sessionManager.updateSession(player, location, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        this.plugin.getLogger().info("[Debug Session] Player " + player.getName() + " moved to " + to.getBlockX() + ", " + to.getBlockY() + ", " + to.getBlockZ());

        this.sessionManager.updateSession(event.getPlayer(), to, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        this.sessionManager.updateSession(event.getPlayer(), event.getTo(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        this.sessionManager.updateSession(event.getPlayer(), event.getRespawnLocation(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        this.sessionManager.updateSession(player, location, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        this.sessionManager.updateSession(player, location, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.sessionManager.removeSession(event.getPlayer());
    }
}