package net.justmoonboy.murderMysteryMoonboy.groupTimer;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;

public class groupTimerManager {
    private static final Random RANDOM = new Random();

    private final MurderMysteryMoonboy plugin;
    private final Map<UUID, Integer> secondsNearSomeone = new HashMap<>();
    private final Set<UUID> warned = new HashSet<>();
    private BukkitTask task;   // <-- this one, make sure it's there

    public groupTimerManager(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) {
            return;
        }
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        secondsNearSomeone.clear();
        warned.clear();
    }

    private void tick() {
        if (!plugin.getRoundManager().isRoundActive() || plugin.getMeetingManager().isMeetingActive()){
            return;
        }

        FileConfiguration config = plugin.getConfig();
        double radius = config.getDouble("groupTimer.radius", 10.0);
        int warnSeconds = config.getInt("groupTimer.warnSeconds", 60);
        int teleportSeconds = config.getInt("groupTimer.teleportSeconds", 120);
        double radiusSquared = radius * radius;

        Player host = plugin.getRoundManager().getHost();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (host != null && player.getUniqueId().equals(host.getUniqueId())) {
                continue;
            }

            boolean nearSomeone = false;
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) {
                    continue;
                }
                if (host != null && other.getUniqueId().equals(host.getUniqueId())) {
                    continue;
                }
                if (!other.getWorld().equals(player.getWorld())) {
                    continue;
                }
                if (player.getLocation().distanceSquared(other.getLocation()) <= radiusSquared) {
                    nearSomeone = true;
                    break;
                }
            }

            UUID id = player.getUniqueId();
            if (nearSomeone) {
                int seconds = secondsNearSomeone.merge(id, 1, Integer::sum);

                if (seconds == warnSeconds && warned.add(id)) {
                    player.showTitle(Title.title(
                            Component.text("Split up!", NamedTextColor.RED),
                            Component.text("You'll be teleported away soon."),
                            Title.Times.times(Duration.ofMillis(300), Duration.ofSeconds(2), Duration.ofMillis(300))
                    ));
                } else if (seconds >= teleportSeconds) {
                    Location safe = findSafeLocation(player.getWorld(), config);
                    if (safe != null) {
                        player.teleport(safe);
                        player.sendMessage("You stayed in a group too long and were moved.");
                    }
                    secondsNearSomeone.put(id, 0);
                    warned.remove(id);
                }
            } else {
                if (secondsNearSomeone.containsKey(id)) {
                }
                secondsNearSomeone.remove(id);
                warned.remove(id);
            }
        }
    }

    private Location findSafeLocation(World world, FileConfiguration config) {
        double minX = config.getDouble("border.minX");
        double maxX = config.getDouble("border.maxX");
        double minZ = config.getDouble("border.minZ");
        double maxZ = config.getDouble("border.maxZ");
        List<Integer> YLevels = config.getIntegerList("border.YLevels");

        for (int attemp = 0; attemp < 20; attemp++) {
            int blockX = (int) Math.floor(minX + RANDOM.nextDouble() * (maxX - minX));
            int blockZ = (int) Math.floor(minZ + RANDOM.nextDouble() * (maxZ - minZ));

            int y;
            if (!YLevels.isEmpty() && RANDOM.nextBoolean()) {
                y = YLevels.get(RANDOM.nextInt(YLevels.size()));
            } else {
                y = world.getHighestBlockYAt(blockX, blockZ);
            }

            Block ground = world.getBlockAt(blockX, y, blockZ);
            Block feet = world.getBlockAt(blockX, y + 1, blockZ);
            Block head = world.getBlockAt(blockX, y + 2, blockZ);

            boolean groundOk = ground.getType().isSolid() && ground.getType() != Material.LAVA;
            boolean feetOk = !feet.getType().isSolid() && feet.getType() != Material.LAVA && feet.getType() != Material.WATER;
            boolean headOk = !head.getType().isSolid();

            if (groundOk && feetOk && headOk) {
                return new Location(world, blockX + 0.5, y + 1, blockZ + 0.5);
            }
        }
        return null;
    }

    public void scatterPlayer(Player player) {
        Location safe = findSafeLocation(player.getWorld(), plugin.getConfig());
        if (safe != null) {
            player.teleport(safe);
        }
    }
}
