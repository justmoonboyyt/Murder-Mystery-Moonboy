package net.justmoonboy.murderMysteryMoonboy.round;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.justmoonboy.murderMysteryMoonboy.phantom.phantomManager;
import net.justmoonboy.murderMysteryMoonboy.shapeshift.shapeshiftManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.security.KeyStore;

public class roundManager {
    private static final long ROUND_DURATION_TICKS = 20L * 60L * 20L;

    private final MurderMysteryMoonboy plugin;
    private static boolean roundActive = false;
    private BukkitTask endTask;

    public roundManager(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    public boolean isRoundActive() {
        return roundActive;
    }

    public Location getSpawnLocation() {
        var config = plugin.getConfig();
        World world = Bukkit.getWorld(config.getString("spawn.world", "world"));
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        float yaw = (float) config.getDouble("spawn.yaw");
        float pitch = (float) config.getDouble("spawn.pitch");
        return new Location(world, x, y ,z, yaw, pitch);
    }

    public Player getHost() {
        var config = plugin.getConfig();
        return Bukkit.getPlayer(config.getString("host", "host"));
    }

    public void startRound(shapeshiftManager ShapeshiftManager, phantomManager PhantomManager) {
        if (roundActive) {
            return;
        }
        roundActive = true;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
        }

        endTask = Bukkit.getScheduler().runTaskLater(plugin, () -> endRound(ShapeshiftManager, PhantomManager), ROUND_DURATION_TICKS);
    }

    public void endRound(shapeshiftManager ShapeshiftManager, phantomManager PhantomManager) {
        if (!roundActive) {
            return;
        }
        roundActive = false;

        if (endTask != null) {
            endTask.cancel();
            endTask = null;
        }

        Location spawn = getSpawnLocation();
        Player host = getHost();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.teleport(spawn);
            ShapeshiftManager.resetRoundData(plugin, player);
            PhantomManager.resetRoundData(plugin, player);
            if (!(player.getName().equals(host.getName()))){
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
                display.remove();
            }
        }
    }
}
