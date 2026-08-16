package net.justmoonboy.murderMysteryMoonboy;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.justmoonboy.murderMysteryMoonboy.command.mmmCommand;
import net.justmoonboy.murderMysteryMoonboy.death.deathListener;
import net.justmoonboy.murderMysteryMoonboy.freeze.freezeListener;
import net.justmoonboy.murderMysteryMoonboy.freeze.freezeManager;
import net.justmoonboy.murderMysteryMoonboy.groupTimer.groupTimerManager;
import net.justmoonboy.murderMysteryMoonboy.gui.*;
import net.justmoonboy.murderMysteryMoonboy.meeting.meetingManager;
import net.justmoonboy.murderMysteryMoonboy.phantom.phantomManager;
import net.justmoonboy.murderMysteryMoonboy.round.roundManager;
import net.justmoonboy.murderMysteryMoonboy.shapeshift.shapeshiftManager;
import net.justmoonboy.murderMysteryMoonboy.tablist.tablistManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderMysteryMoonboy extends JavaPlugin {

    private shapeshiftManager ShapeshiftManager;
    private roundManager RoundManager;
    private phantomManager PhantomManager;
    private freezeManager FreezeManager;
    private tablistManager TablistManager;
    private groupTimerManager GroupTimerManager;
    private meetingManager MeetingManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ShapeshiftManager = new shapeshiftManager();
        PhantomManager = new phantomManager(this);
        RoundManager = new roundManager(this);
        FreezeManager = new freezeManager();
        TablistManager = new tablistManager(this);
        GroupTimerManager = new groupTimerManager(this);
        MeetingManager = new meetingManager(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(mmmCommand.create(this), "Main plugin command"));
        Bukkit.getPluginManager().registerEvents(new playerHeadClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new shapeshiftItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new phantomItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new deathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new freezeListener(FreezeManager), this);
        Bukkit.getPluginManager().registerEvents(new murderItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new vigilanteItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new blindnessItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new voteGUIListener(this), this);

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
        }
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

    public groupTimerManager getGroupTimerManager() {
        return GroupTimerManager;
    }

    public meetingManager getMeetingManager(){
        return MeetingManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
