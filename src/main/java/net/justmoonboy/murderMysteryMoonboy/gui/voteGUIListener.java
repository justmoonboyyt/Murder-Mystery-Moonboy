package net.justmoonboy.murderMysteryMoonboy.gui;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.justmoonboy.murderMysteryMoonboy.meeting.meetingManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class voteGUIListener implements Listener {
    private static final String VOTE_TARGET_KEY = "vote_target";

    private final MurderMysteryMoonboy plugin;

    public voteGUIListener(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!verdictBell.isVerdictBell(plugin, event.getItem())) {
            return;
        }
        event.setCancelled(true);
        openVoteMenu(event.getPlayer());
    }

    private void openVoteMenu(Player voter) {
        var eligible = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getGameMode() != GameMode.SPECTATOR)
                .filter(p -> plugin.getRoundManager().getHost() == null || !p.getUniqueId().equals(plugin.getRoundManager().getHost().getUniqueId()))
                .toList();

        int size = ((eligible.size() + 1 + 8) / 9) * 9;
        Inventory menu = Bukkit.createInventory(new VoteMenuHolder(), Math.max(9, size), Component.text("Cast Your Vote"));

        for (Player target : eligible) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(target);
            meta.setDisplayName(target.getName());
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, VOTE_TARGET_KEY), PersistentDataType.STRING, target.getUniqueId().toString());
            head.setItemMeta(meta);
            menu.addItem(head);
        }

        ItemStack skip = new ItemStack(Material.BARRIER);
        ItemMeta skipMeta = skip.getItemMeta();
        skipMeta.setDisplayName("Skip Vote");
        skipMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, VOTE_TARGET_KEY), PersistentDataType.STRING, meetingManager.SKIP_VOTE.toString());
        skip.setItemMeta(skipMeta);
        menu.addItem(skip);

        voter.openInventory(menu);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof VoteMenuHolder)) {
            return;
        }
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }
        ItemMeta meta = clicked.getItemMeta();
        String targetString = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, VOTE_TARGET_KEY), PersistentDataType.STRING);
        if (targetString == null || !(event.getWhoClicked() instanceof Player voter)) {
            return;
        }

        plugin.getMeetingManager().castVote(voter, UUID.fromString(targetString));
    }

    private static class VoteMenuHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
