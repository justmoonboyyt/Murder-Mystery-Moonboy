package net.justmoonboy.murderMysteryMoonboy.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class phantomItem {

    public static final String KEY = "phantom_item";

    public static ItemStack create(Plugin plugin) {
        ItemStack item = new ItemStack(Material.GHAST_TEAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Veil of the Phantom");
        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, KEY), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isPhantomItem(Plugin plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(plugin, KEY), PersistentDataType.BYTE);
    }
}
