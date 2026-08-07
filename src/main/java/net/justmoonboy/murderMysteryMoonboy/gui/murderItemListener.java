package net.justmoonboy.murderMysteryMoonboy.gui;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.sql.PreparedStatement;

public class murderItemListener implements Listener {
    private static final int COOLDOWN_TICKS = 200; // 10 seconds * 20 ticks/sec
    private static final double REACH = 5.0;

    private final MurderMysteryMoonboy plugin;

    public murderItemListener(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if (!murderWeapon.isMurderItem(plugin, item)) {
                return;
            }
            event.setCancelled(true);

            boolean isPhantom = plugin.getPhantomManager().hasRole(player.getUniqueId());
            boolean isShapeshifter = plugin.getShapeshiftManager().hasRole(player.getUniqueId());
            if (!isPhantom && !isShapeshifter) {
                player.sendMessage("Only murderers can use this.");
                return;
            }

            if (player.hasCooldown(item)) {
                double secondsLeft = player.getCooldown(item) / 20.0;
                player.sendMessage("Your blade is still recharging with " + secondsLeft + " seconds left.");
                return;
            }

            Player target = getTarget(player);
            if (target == null) {
                player.sendMessage("No target in range.");
                return;
            }

            target.setHealth(0.0);
            player.sendMessage("You have killed " + target.getName() + ".");

            player.setCooldown(item, COOLDOWN_TICKS);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if (!murderWeapon.isMurderItem(plugin, item)) {
                return;
            }
            event.setCancelled(true);

            boolean isPhantom = plugin.getPhantomManager().hasRole(player.getUniqueId());
            boolean isShapeshifter = plugin.getShapeshiftManager().hasRole(player.getUniqueId());
            if (!isPhantom && !isShapeshifter) {
                player.sendMessage("Only murderers can use this.");
                return;
            }

            player.sendMessage("Use right click for insant kill.");
        }
    }

    private Player getTarget(Player player) {
        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                REACH,
                0.3,
                entity -> entity instanceof Player && !entity.equals(player)
        );

        if (result == null || !(result.getHitEntity() instanceof Player)) {
            return null;
        }
        return (Player) result.getHitEntity();
    }
}
