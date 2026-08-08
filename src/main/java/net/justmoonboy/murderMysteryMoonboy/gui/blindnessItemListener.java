package net.justmoonboy.murderMysteryMoonboy.gui;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class blindnessItemListener implements Listener {
    private static final int COOLDOWN_TICKS = 600;
    private static final int LENGTH_SECONDS = 10;

    private final MurderMysteryMoonboy plugin;

    public blindnessItemListener(MurderMysteryMoonboy plugin) {
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

            if (!blindnessItem.isBlindnessItem(plugin, item)) {
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
                player.sendMessage("This device is still recharging with " + secondsLeft + " seconds left.");
                return;
            }

            Player host = getHost();
            for (Player target : Bukkit.getOnlinePlayers()) {
                boolean targetIsMurderer = plugin.getPhantomManager().hasRole(target.getUniqueId()) || plugin.getShapeshiftManager().hasRole(target.getUniqueId());
                if (target.getName().equals(host.getName()) || targetIsMurderer) {
                    continue;
                }
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, LENGTH_SECONDS * 20, 0, true, true));
                target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, LENGTH_SECONDS * 20, 0, true, true));
            }
            player.setCooldown(item, COOLDOWN_TICKS);
        }
    }

    public Player getHost() {
        var config = plugin.getConfig();
        return Bukkit.getPlayer(config.getString("host", "host"));
    }
}
