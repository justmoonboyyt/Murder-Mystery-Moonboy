package net.justmoonboy.murderMysteryMoonboy.freeze;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class freezeListener implements Listener {
    private final freezeManager manager;

    public freezeListener(freezeManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!manager.isFrozen(player)) {
            return;
        }

        Location to = event.getTo();
        Location frozen = manager.getFrozenLocation(player);
        if (to == null || frozen == null) {
            return;
        }

        if (to.getX() != frozen.getX() || to.getY() != frozen.getY() || to.getZ() != frozen.getZ()|| to.getYaw() != frozen.getYaw() || to.getPitch() != frozen.getPitch()) {
            event.setTo(frozen);
        }
    }
}
