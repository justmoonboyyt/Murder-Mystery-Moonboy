package net.justmoonboy.murderMysteryMoonboy.gui;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class shapeshiftItemListener implements Listener {
    private final MurderMysteryMoonboy plugin;

    public shapeshiftItemListener(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!shapeshiftItem.isShapeshiftItem(plugin, event.getItem())){
            return;
        }

        if (!plugin.getShapeshiftManager().hasRole(player.getUniqueId())) {
            player.sendMessage("Only shapeshifters can use this.");
            return;
        }

        playerListGui.open(player);
    }
}
