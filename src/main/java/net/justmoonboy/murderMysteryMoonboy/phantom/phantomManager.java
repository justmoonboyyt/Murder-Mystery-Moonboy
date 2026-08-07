package net.justmoonboy.murderMysteryMoonboy.phantom;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.justmoonboy.murderMysteryMoonboy.gui.phantomItem;
import net.justmoonboy.murderMysteryMoonboy.gui.murderWeapon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class phantomManager {
    private static final int MAX_INVIS_PER_ROUND = 10;
    private static final long INVIS_DURATION_TICKS = 20L * 60L;

    private final Set<UUID> phantoms = new HashSet<>();
    private final Set<UUID> currentlyInvis = new HashSet<>();
    private final Map<UUID, BukkitTask> revertTasks = new HashMap<>();
    private final Map<UUID, Integer> invisCounts = new HashMap<>();

    private final MurderMysteryMoonboy plugin;

    public phantomManager(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    public void giveRole(UUID playerId) {
        phantoms.add(playerId);
    }

    public void removeRole(Player player, MurderMysteryMoonboy plugin) {
        phantoms.remove(player.getUniqueId());
        removePhantomItem(player, plugin);
    }

    public boolean hasRole(UUID playerId) {
        return phantoms.contains(playerId);
    }

    public void clearAllPhantoms(MurderMysteryMoonboy plugin) {
        for (UUID id : phantoms) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                removePhantomItem(player, plugin);
            }
        }
        phantoms.clear();
    }

    private void removePhantomItem(Player player, MurderMysteryMoonboy plugin) {
        PlayerInventory inv = player.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (phantomItem.isPhantomItem(plugin, item)) {
                inv.remove(item);
            }
            if (murderWeapon.isMurderItem(plugin, item)) {
                inv.remove(item);
            }
        }
    }

    public String canInvis(Player viewer) {
        if (!hasRole(viewer.getUniqueId())) {
            return "You dont have the phantom role.";
        }
        if (currentlyInvis.contains(viewer.getUniqueId())) {
            return "You cant invis while already invis";
        }
        int used = invisCounts.getOrDefault(viewer.getUniqueId(), 0);
        if (used >= MAX_INVIS_PER_ROUND) {
            return "You've used all " + MAX_INVIS_PER_ROUND + " disappearances this round.";
        }
        return null;
    }

    public String canUnInvis(Player viewer) {
        if (!hasRole(viewer.getUniqueId())) {
            return "You dont have the phantom role.";
        }
        if (!viewer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return "You are already visible";
        }
        return null;
    }

    public void startInvis(Player viewer, Plugin plugin) {
        UUID id = viewer.getUniqueId();

        currentlyInvis.add(id);
        invisCounts.merge(id, 1, Integer::sum);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give " + viewer.getName() + " minecraft:invisibility 60 1 true");
        viewer.sendMessage("You are now invis for 1 minute.");

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            revertTasks.remove(id);
            currentlyInvis.remove(id);
            viewer.sendMessage("You are now visible.");
        }, INVIS_DURATION_TICKS);

        revertTasks.put(id, task);
    }

    public void revertInvis(Player viewer) {
        UUID id = viewer.getUniqueId();

        BukkitTask pending = revertTasks.remove(id);
        if (pending != null) {
            pending.cancel();
        }
        currentlyInvis.remove(id);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect clear " + viewer.getName());
        viewer.sendMessage("You are now visible.");
    }

    public void resetRoundData(MurderMysteryMoonboy plugin, Player viewer) {
        invisCounts.clear();
        for (BukkitTask task : revertTasks.values()) {
            task.cancel();
        }
        revertTasks.clear();
        revertInvis(viewer);
        clearAllPhantoms(plugin);
    }
}
