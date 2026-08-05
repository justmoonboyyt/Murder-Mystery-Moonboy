package net.justmoonboy.murderMysteryMoonboy.gui;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class phantomItemListener implements Listener {
    private final MurderMysteryMoonboy plugin;

    public phantomItemListener(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!phantomItem.isPhantomItem(plugin, event.getItem())){
            return;
        }

        if (!plugin.getPhantomManager().hasRole(player.getUniqueId())) {
            player.sendMessage("Only phantoms can use this.");
            return;
        }

        String denialReason = plugin.getPhantomManager().canInvis(player);
        if (denialReason != null) {
            player.sendMessage(denialReason);
            return;
        }

        plugin.getPhantomManager().startInvis(player, plugin);
    }
}
