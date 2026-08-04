package net.justmoonboy.murderMysteryMoonboy.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.justmoonboy.murderMysteryMoonboy.gui.playerListGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class mmmCommand {
    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("mmm")
                .then(Commands.literal("test")
                        .then(Commands.literal("list")
                                .executes(ctx -> {
                                    if (ctx.getSource().getSender() instanceof Player player) {
                                        playerListGui.open(player);
                                    } else {
                                        ctx.getSource().getSender().sendMessage("Only players can open the GUI");
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }
}
