package net.justmoonboy.murderMysteryMoonboy.gui;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public class playerHeadClickListener implements Listener {
    private final MurderMysteryMoonboy plugin;

    public playerHeadClickListener(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!"Select Player".equals(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player viewer)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !(clicked.getItemMeta() instanceof SkullMeta meta)) {
            return;
        }

        if (meta.getOwningPlayer() == null) {
            return;
        }

        String targetName = meta.getOwningPlayer().getName();
        if (targetName == null) {
            return;
        }

        String denialReason = plugin.getShapeshiftManager().canShapeshift(viewer);
        if (denialReason != null) {
            viewer.sendMessage(denialReason);
            return;
        }

        viewer.closeInventory();
        if (targetName.equals(viewer.getName())) {
            plugin.getShapeshiftManager().revertShapeshift(viewer);
        }
        else{
            plugin.getShapeshiftManager().startShapeshift(viewer, targetName, plugin);
        }
    }
}
