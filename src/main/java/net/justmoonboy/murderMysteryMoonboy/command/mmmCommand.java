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
import net.justmoonboy.murderMysteryMoonboy.gui.phantomItem;
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
                .then(uninvisBranch(plugin))
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

                                        if (!plugin.getRoundManager().isRoundActive()){
                                            target.sendMessage("Round must be started to give a role.");
                                        }
                                        else {
                                            plugin.getShapeshiftManager().giveRole(target.getUniqueId());
                                            target.getInventory().addItem(shapeshiftItem.create(plugin));
                                            target.sendMessage("You are now a Shapeshifter.");
                                            ctx.getSource().getSender().sendMessage("Gave shapeshifter role to " + target.getName());
                                        }
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
                                        }))))
                .then(Commands.literal("phantom")
                        .then(Commands.literal("add")
                                .then(Commands.argument("target", ArgumentTypes.player())
                                        .executes(ctx -> {
                                            PlayerSelectorArgumentResolver resolver =
                                                    ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                                            Player target = resolver.resolve(ctx.getSource()).get(0);

                                            if (!plugin.getRoundManager().isRoundActive()){
                                                target.sendMessage("Round must be started to give a role.");
                                            }
                                            else {
                                                plugin.getPhantomManager().giveRole(target.getUniqueId());
                                                target.getInventory().addItem(phantomItem.create(plugin));
                                                target.sendMessage("You are now a Phantom.");
                                                ctx.getSource().getSender().sendMessage("Gave phantom role to " + target.getName());
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        ).then(Commands.literal("remove")
                                .then(Commands.argument("target", ArgumentTypes.player())
                                        .executes(ctx -> {
                                            PlayerSelectorArgumentResolver resolver =
                                                    ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                                            Player target = resolver.resolve(ctx.getSource()).get(0);

                                            plugin.getPhantomManager().removeRole(target, plugin);
                                            target.sendMessage("Your Phantom role has been removed.");
                                            ctx.getSource().getSender().sendMessage("Removed phantom role from " + target.getName());

                                            return Command.SINGLE_SUCCESS;
                                        }))));

    }

    public static LiteralArgumentBuilder<CommandSourceStack> roundBranch(MurderMysteryMoonboy plugin) {
        return Commands.literal("round")
                .then(Commands.literal("start")
                        .executes(ctx -> {
                            plugin.getRoundManager().startRound(plugin.getShapeshiftManager(), plugin.getPhantomManager());
                            ctx.getSource().getSender().sendMessage("Round started.");
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("stop")
                        .executes(ctx -> {
                            plugin.getRoundManager().endRound(plugin.getShapeshiftManager(), plugin.getPhantomManager());
                            ctx.getSource().getSender().sendMessage("Round stopped.");
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> unshiftBranch(MurderMysteryMoonboy plugin) {
        return Commands.literal("unshift")
                .then(Commands.argument("target", ArgumentTypes.players())
                        .executes(ctx -> {
                            PlayerSelectorArgumentResolver resolver = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                            for (Player target : resolver.resolve(ctx.getSource())) {
                                plugin.getShapeshiftManager().revertShapeshift(target);
                                ctx.getSource().getSender().sendMessage("Reverted " + target.getName() + "'s disguise.");
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> uninvisBranch(MurderMysteryMoonboy plugin) {
        return Commands.literal("uninvis")
                .then(Commands.argument("target", ArgumentTypes.players())
                        .executes(ctx -> {
                            PlayerSelectorArgumentResolver resolver = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                            for (Player target : resolver.resolve(ctx.getSource())) {
                                plugin.getPhantomManager().revertInvis(target);
                                ctx.getSource().getSender().sendMessage("Reverted " + target.getName() + "'s invis.");
                            }
                            return  Command.SINGLE_SUCCESS;
                        }));
    }
}
