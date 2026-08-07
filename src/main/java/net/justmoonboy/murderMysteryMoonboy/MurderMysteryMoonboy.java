package net.justmoonboy.murderMysteryMoonboy;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.justmoonboy.murderMysteryMoonboy.command.mmmCommand;
import net.justmoonboy.murderMysteryMoonboy.death.deathListener;
import net.justmoonboy.murderMysteryMoonboy.freeze.freezeListener;
import net.justmoonboy.murderMysteryMoonboy.freeze.freezeManager;
import net.justmoonboy.murderMysteryMoonboy.gui.phantomItemListener;
import net.justmoonboy.murderMysteryMoonboy.gui.playerHeadClickListener;
import net.justmoonboy.murderMysteryMoonboy.gui.shapeshiftItemListener;
import net.justmoonboy.murderMysteryMoonboy.phantom.phantomManager;
import net.justmoonboy.murderMysteryMoonboy.round.roundManager;
import net.justmoonboy.murderMysteryMoonboy.shapeshift.shapeshiftManager;
import net.justmoonboy.murderMysteryMoonboy.tablist.tablistManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderMysteryMoonboy extends JavaPlugin {

    private shapeshiftManager ShapeshiftManager;
    private roundManager RoundManager;
    private phantomManager PhantomManager;
    private freezeManager FreezeManager;
    private tablistManager TablistManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ShapeshiftManager = new shapeshiftManager();
        PhantomManager = new phantomManager();
        RoundManager = new roundManager(this);
        FreezeManager = new freezeManager();
        TablistManager = new tablistManager(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(mmmCommand.create(this), "Main plugin command"));
        Bukkit.getPluginManager().registerEvents(new playerHeadClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new shapeshiftItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new phantomItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new deathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new freezeListener(FreezeManager), this);
    }

    public shapeshiftManager getShapeshiftManager() {
        return ShapeshiftManager;
    }

    public roundManager getRoundManager() {
        return RoundManager;
    }

    public phantomManager getPhantomManager() {
        return  PhantomManager;
    }

    public freezeManager getFreezeManager() {
        return FreezeManager;
    }

    public tablistManager getTablistManager() {
        return TablistManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
