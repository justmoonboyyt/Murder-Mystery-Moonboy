package net.justmoonboy.murderMysteryMoonboy.tablist;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class tablistManager {
    private final MurderMysteryMoonboy plugin;

    public tablistManager(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    public Player getHost() {
        var config = plugin.getConfig();
        return Bukkit.getPlayer(config.getString("host", "host"));
    }

    Player host = getHost();
    public void applyTablistRestriction(Collection<? extends Player> onlinePlayers) {
        for (Player viewer : onlinePlayers) {
            if (viewer.equals(host)) {
                continue;
            }
            for (Player other : onlinePlayers) {
                if (!other.equals(viewer)) {
                    viewer.unlistPlayer(other);
                }
            }
        }
    }

    public void resetTablist(Collection<? extends Player> onlinePlayers) {
        for (Player viewer : onlinePlayers) {
            for (Player other : onlinePlayers) {
                if (!other.equals(viewer)) {
                    viewer.listPlayer(other);
                }
            }
        }
    }
}
