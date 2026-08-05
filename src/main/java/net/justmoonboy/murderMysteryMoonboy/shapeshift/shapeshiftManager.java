package net.justmoonboy.murderMysteryMoonboy.shapeshift;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.justmoonboy.murderMysteryMoonboy.gui.shapeshiftItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class shapeshiftManager {
    private static final int MAX_SHIFTS_PER_ROUND = 10;
    private static final long DISGUISE_DURATION_TICKS = 20L * 60L;

    private final Set<UUID> shapeshifters = new HashSet<>();
    private final Set<UUID> currentlyShapeshifted = new HashSet<>();
    private final Map<UUID, BukkitTask> revertTasks = new HashMap<>();
    private final Map<UUID, Integer> shiftCounts = new HashMap<>();

    public void giveRole(UUID playerId) {
        shapeshifters.add(playerId);
    }

    public void removeRole(Player player, MurderMysteryMoonboy plugin) {
        shapeshifters.remove(player.getUniqueId());
        removeShapeshiftItem(player, plugin);
    }

    public boolean hasRole(UUID playerId) {
        return shapeshifters.contains(playerId);
    }

    public void clearAllShapeshifters(MurderMysteryMoonboy plugin) {
        for (UUID id : shapeshifters) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                removeShapeshiftItem(player, plugin);
            }
        }
        shapeshifters.clear();
    }

    private void removeShapeshiftItem(Player player, MurderMysteryMoonboy plugin) {
        PlayerInventory inv = player.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (shapeshiftItem.isShapeshiftItem(plugin, item)) {
                inv.remove(item);
            }
        }
    }

    public String canShapeshift(Player viewer) {
        if (!hasRole(viewer.getUniqueId())) {
            return "You dont have the shapeshifter role.";
        }
        if (currentlyShapeshifted.contains(viewer.getUniqueId())) {
            return "You cant shapeshift while already shapeshifted";
        }
        int used = shiftCounts.getOrDefault(viewer.getUniqueId(), 0);
        if (used >= MAX_SHIFTS_PER_ROUND) {
            return "You've used all " + MAX_SHIFTS_PER_ROUND + " shapeshifts this round.";
        }
        return null;
    }

    public void startShapeshift(Player viewer, String targetSkinName, Plugin plugin) {
        UUID id = viewer.getUniqueId();

        currentlyShapeshifted.add(id);
        shiftCounts.merge(id, 1, Integer::sum);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + targetSkinName + " " + viewer.getName());
        viewer.sendMessage("You now look like " + targetSkinName + " for 1 minute.");

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            revertTasks.remove(id);
            currentlyShapeshifted.remove(id);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin clear " + viewer.getName());
            viewer.sendMessage("Your skin has been reverted.");
        }, DISGUISE_DURATION_TICKS);

        revertTasks.put(id, task);
    }

    public void revertShapeshift(Player viewer) {
        UUID id = viewer.getUniqueId();

        BukkitTask pending = revertTasks.remove(id);
        if (pending != null) {
            pending.cancel();
        }
        currentlyShapeshifted.remove(id);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin clear " + viewer.getName());
        viewer.sendMessage("Your skin has reverted to normal.");
    }

    public void resetRoundData(MurderMysteryMoonboy plugin) {
        shiftCounts.clear();
        for (BukkitTask task : revertTasks.values()) {
            task.cancel();
        }
        revertTasks.clear();
        currentlyShapeshifted.clear();
        clearAllShapeshifters(plugin);
    }
}
