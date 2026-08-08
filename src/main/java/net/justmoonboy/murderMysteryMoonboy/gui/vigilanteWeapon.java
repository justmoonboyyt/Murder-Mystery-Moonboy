package net.justmoonboy.murderMysteryMoonboy.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class vigilanteWeapon {

    public static final String KEY = "vigilante_item";

    public static ItemStack create(Plugin plugin) {
        ItemStack item = new ItemStack(Material.BAMBOO);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Vigilante Knife");
        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, KEY), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isVigilanteItem(Plugin plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(plugin, KEY), PersistentDataType.BYTE);
    }
}
