package net.justmoonboy.murderMysteryMoonboy.gui;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class murderItemListener implements Listener {
    private final MurderMysteryMoonboy plugin;

    public murderItemListener(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            Player player = event.getPlayer();
            if (!murderWeapon.isMurderItem(plugin, event.getItem())){
                return;
            }

            if (!plugin.getPhantomManager().hasRole(player.getUniqueId()) || !plugin.getShapeshiftManager().hasRole(player.getUniqueId())) {
                player.sendMessage("Only murderers can use this.");
                return;
            }

            String denialReason = plugin.getPhantomManager().canInvis(player);
            if (denialReason != null) {
                player.sendMessage(denialReason);
                return;
            }

            plugin.getPhantomManager().startInvis(player, plugin);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (!phantomItem.isPhantomItem(plugin, event.getItem())){
                return;
            }

            if (!plugin.getPhantomManager().hasRole(player.getUniqueId())) {
                player.sendMessage("Only phantoms can use this.");
                return;
            }

            String denialReason = plugin.getPhantomManager().canUnInvis(player);
            if (denialReason != null) {
                player.sendMessage(denialReason);
                return;
            }

            plugin.getPhantomManager().revertInvis(player);
        }

    }
}
