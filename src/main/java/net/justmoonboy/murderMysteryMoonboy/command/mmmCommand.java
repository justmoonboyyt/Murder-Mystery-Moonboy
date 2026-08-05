package net.justmoonboy.murderMysteryMoonboy.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.justmoonboy.murderMysteryMoonboy.gui.shapeshiftItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class mmmCommand {

    public static LiteralCommandNode<CommandSourceStack> create(MurderMysteryMoonboy plugin) {
        return Commands.literal("mmm")
                .then(roleBranch(plugin))
                .then(roundBranch(plugin))
                .then(unshiftBranch(plugin))
                .build();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> roleBranch(MurderMysteryMoonboy plugin) {
        return Commands.literal("role")
                .then(Commands.literal("shapeshifter")
                        .then(Commands.literal("add")
                            .then(Commands.argument("target", ArgumentTypes.player())
                                    .executes(ctx -> {
                                        PlayerSelectorArgumentResolver resolver =
                                                ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                                        Player target = resolver.resolve(ctx.getSource()).get(0);

                                        plugin.getShapeshiftManager().giveRole(target.getUniqueId());
                                        target.getInventory().addItem(shapeshiftItem.create(plugin));
                                        target.sendMessage("You are now a Shapeshifter!");
                                        ctx.getSource().getSender().sendMessage("Gave shapeshifter role to " + target.getName());

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    ).then(Commands.literal("remove")
                                .then(Commands.argument("target", ArgumentTypes.player())
                                        .executes(ctx -> {
                                            PlayerSelectorArgumentResolver resolver =
                                                    ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                                            Player target = resolver.resolve(ctx.getSource()).get(0);

                                            plugin.getShapeshiftManager().removeRole(target, plugin);
                                            target.sendMessage("Your Shapeshifter role has been removed.");
                                            ctx.getSource().getSender().sendMessage("Removed shapeshifter role from " + target.getName());

                                            return Command.SINGLE_SUCCESS;
                                        }))));

    }

    public static LiteralArgumentBuilder<CommandSourceStack> roundBranch(MurderMysteryMoonboy plugin) {
        return Commands.literal("round")
                .then(Commands.literal("start")
                        .executes(ctx -> {
                            plugin.getRoundManager().startRound(plugin.getShapeshiftManager());
                            ctx.getSource().getSender().sendMessage("Round started.");
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("stop")
                        .executes(ctx -> {
                            plugin.getRoundManager().endRound(plugin.getShapeshiftManager());
                            ctx.getSource().getSender().sendMessage("Round stopped.");
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> unshiftBranch(MurderMysteryMoonboy plugin) {
        return Commands.literal("unshift")
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            PlayerSelectorArgumentResolver resolver = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                            Player target = resolver.resolve(ctx.getSource()).get(0);
                            plugin.getShapeshiftManager().revertShapeshift(target);
                            ctx.getSource().getSender().sendMessage("Reverted " + target.getName() + "'s disguise.");
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }
}
