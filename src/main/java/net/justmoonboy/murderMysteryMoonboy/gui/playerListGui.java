package net.justmoonboy.murderMysteryMoonboy.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;

public class playerListGui {
    public static void open(Player viewer) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        int size = Math.min(54, ((onlinePlayers.size() / 9) +1 ) * 9);

        Inventory gui = Bukkit.createInventory(null, size, "Select Player");

        for (Player target : onlinePlayers) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            meta.setOwningPlayer(target);
            meta.setDisplayName(target.getName());

            head.setItemMeta(meta);
            gui.addItem(head);
        }
        viewer.openInventory(gui);
    }
}
