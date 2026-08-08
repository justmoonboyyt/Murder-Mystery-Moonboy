package net.justmoonboy.murderMysteryMoonboy.death;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.justmoonboy.murderMysteryMoonboy.round.roundManager;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class deathListener implements Listener {
    private final MurderMysteryMoonboy plugin;

    public deathListener(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    public Location getSpawnLocation() {
        var config = plugin.getConfig();
        World world = Bukkit.getWorld(config.getString("spawn.world", "world"));
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        float yaw = (float) config.getDouble("spawn.yaw");
        float pitch = (float) config.getDouble("spawn.pitch");
        return new Location(world, x, y ,z, yaw, pitch);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!plugin.getRoundManager().isRoundActive()) {
            event.setCancelled(true);
            Player player = (Player) event.getEntity();
            Location spawn = getSpawnLocation();
            player.teleport(spawn);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        World world = deathLocation.getWorld();
        for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext();) {
            ItemStack drop = iterator.next();
            Material type = drop.getType();
            if (type == Material.COOKED_BEEF || type == Material.STICK || type == Material.BLAZE_ROD || type == Material.GHAST_TEAR || type == Material.GLOWSTONE_DUST) {
                iterator.remove();
                event.getItemsToKeep().add(drop);
            }
        }
        if (!plugin.getRoundManager().isRoundActive()) {
            return;
        }
        Location textLocation = new Location(world, deathLocation.getX(), deathLocation.getY() + 1, deathLocation.getZ(), 0, 0);
        summonText(world, textLocation, player.getName());
    }

    public void summonText(World world, Location textLocation, String name) {
        TextDisplay deathDisplay = world.spawn(textLocation, TextDisplay.class, entity -> {
            entity.text(Component.text(name));
            entity.setBillboard(Display.Billboard.VERTICAL);
        });
    }
}
