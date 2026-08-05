package net.justmoonboy.murderMysteryMoonboy;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.justmoonboy.murderMysteryMoonboy.command.mmmCommand;
import net.justmoonboy.murderMysteryMoonboy.gui.playerHeadClickListener;
import net.justmoonboy.murderMysteryMoonboy.gui.shapeshiftItemListener;
import net.justmoonboy.murderMysteryMoonboy.round.roundManager;
import net.justmoonboy.murderMysteryMoonboy.shapeshift.shapeshiftManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderMysteryMoonboy extends JavaPlugin {

    private shapeshiftManager ShapeshiftManager;
    private roundManager RoundManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ShapeshiftManager = new shapeshiftManager();
        RoundManager = new roundManager(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(mmmCommand.create(this), "Main plugin command"));
        Bukkit.getPluginManager().registerEvents(new playerHeadClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new shapeshiftItemListener(this), this);
    }

    public shapeshiftManager getShapeshiftManager() {
        return ShapeshiftManager;
    }

    public roundManager getRoundManager() {
        return RoundManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
