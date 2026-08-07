package net.justmoonboy.murderMysteryMoonboy.round;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.justmoonboy.murderMysteryMoonboy.gui.murderWeapon;
import net.justmoonboy.murderMysteryMoonboy.gui.phantomItem;
import net.justmoonboy.murderMysteryMoonboy.gui.shapeshiftItem;
import net.justmoonboy.murderMysteryMoonboy.phantom.phantomManager;
import net.justmoonboy.murderMysteryMoonboy.shapeshift.shapeshiftManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    private static final Random RANDOM = new Random();

    public int startRound(int murdererCount, shapeshiftManager ShapeshiftManager, phantomManager PhantomManager) {
        if (roundActive) {
            return -1;
        }
        roundActive = true;

        plugin.getTablistManager().applyTablistRestriction(Bukkit.getOnlinePlayers());

        Player host = getHost();
        List<Player> eligible = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
            if (host == null || !player.getUniqueId().equals(host.getUniqueId())) {
                eligible.add(player);
            }
        }

        Collections.shuffle(eligible);
        int actualCount = Math.min(murdererCount, eligible.size());
        List<Player> murderers = eligible.subList(0, actualCount);

        for (Player murderer : murderers) {
            if (RANDOM.nextBoolean()) {
                PhantomManager.giveRole(murderer.getUniqueId());
                murderer.getInventory().addItem(phantomItem.create(plugin));
                murderer.getInventory().addItem(murderWeapon.create(plugin));
                showRoleTitle(murderer, "Phantom");
            } else {
                ShapeshiftManager.giveRole(murderer.getUniqueId());
                murderer.getInventory().addItem(shapeshiftItem.create(plugin));
                murderer.getInventory().addItem(murderWeapon.create(plugin));
                showRoleTitle(murderer, "Shapeshifter");
            }
        }

        endTask = Bukkit.getScheduler().runTaskLater(plugin, () -> endRound(ShapeshiftManager, PhantomManager), ROUND_DURATION_TICKS);
        return murdererCount;
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
        plugin.getTablistManager().resetTablist(Bukkit.getOnlinePlayers());
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
                display.remove();
            }
        }
    }

    private void showRoleTitle(Player player, String roleName) {
        Title title = Title.title(
                Component.text("You are the " + roleName, NamedTextColor.DARK_RED),
                Component.text("Eliminate everyone without getting caught."),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
        );
        player.showTitle(title);
    }
}
