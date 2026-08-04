package net.justmoonboy.murderMysteryMoonboy;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import net.justmoonboy.murderMysteryMoonboy.command.mmmCommand;

public final class MurderMysteryMoonboy extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(mmmCommand.create(), "Main plugin command");
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
