package net.justmoonboy.murderMysteryMoonboy.freeze;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class freezeManager {
    private final Map<UUID, Location> frozenPlayers = new HashMap<>();

    public void freeze(Player player) {
        frozenPlayers.put(player.getUniqueId(), player.getLocation());
    }

    public void unfreeze(Player player) {
        frozenPlayers.remove(player.getUniqueId());
    }

    public boolean isFrozen(Player player) {
        return frozenPlayers.containsKey(player.getUniqueId());
    }

    public Location getFrozenLocation(Player player) {
        return frozenPlayers.get(player.getUniqueId());
    }
}
