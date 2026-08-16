package net.justmoonboy.murderMysteryMoonboy.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class verdictBell {

    public static final String KEY = "verdict_bell";

    public static ItemStack create(Plugin plugin) {
        ItemStack item = new ItemStack(Material.BELL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Verdict Bell");
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, KEY), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isVerdictBell(Plugin plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, KEY), PersistentDataType.BYTE);
    }
}
