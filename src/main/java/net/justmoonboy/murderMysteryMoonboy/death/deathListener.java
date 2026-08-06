package net.justmoonboy.murderMysteryMoonboy.death;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class deathListener implements Listener {
    private final MurderMysteryMoonboy plugin;

    public deathListener(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String name = player.getName();
        Location deathLocation = player.getLocation();

        double x = deathLocation.getX();
        double y = deathLocation.getY() + 1;
        double z = deathLocation.getZ();
        World world = deathLocation.getWorld();
        Location textLocation = new Location(world, x, y, z, 0, 0);
        summonText(world, textLocation, name);
    }

    public void summonText(World world, Location textLocation, String name) {
        TextDisplay deathDisplay = world.spawn(textLocation, TextDisplay.class, entity -> {
            entity.text(Component.text(name));
            entity.setBillboard(Display.Billboard.VERTICAL);
        });
    }
}
