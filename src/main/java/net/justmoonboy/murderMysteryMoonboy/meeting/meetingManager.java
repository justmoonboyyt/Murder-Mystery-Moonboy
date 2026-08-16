package net.justmoonboy.murderMysteryMoonboy.meeting;

import net.justmoonboy.murderMysteryMoonboy.MurderMysteryMoonboy;
import net.justmoonboy.murderMysteryMoonboy.gui.verdictBell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;

public class meetingManager {
    public static final UUID SKIP_VOTE = new UUID(0, 0);

    private static final long EXPLANATION_TICKS = 20L * 60L * 2L;
    private static final long VOTING_TICKS = 20L * 15L;
    private static final long FREEZE_TICKS = 20L * 3L;

    private final MurderMysteryMoonboy plugin;

    private boolean meetingActive = false;
    private final Map<UUID, PlayerSnapshot> snapshots = new HashMap<>();
    private final Map<UUID, UUID> votes = new HashMap<>();
    private List<UUID> eligiblePlayers = new ArrayList<>();
    private BukkitTask phaseTask;

    public meetingManager(MurderMysteryMoonboy plugin) {
        this.plugin = plugin;
    }

    public boolean isMeetingActive() {
        return meetingActive;
    }

    private static class PlayerSnapshot {
        ItemStack[] contents;
        ItemStack[] armor;
        ItemStack offHand;
    }

    public void callMeeting(Player caller) {
        if (meetingActive || !plugin.getRoundManager().isRoundActive()) {
            return;
        }
        meetingActive = true;
        votes.clear();
        snapshots.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getShapeshiftManager().hasRole(player.getUniqueId())) {
                plugin.getShapeshiftManager().revertShapeshift(player);
            }
            if (plugin.getPhantomManager().hasRole(player.getUniqueId())) {
                plugin.getPhantomManager().revertInvis(player);
            }
        }

        Player host = plugin.getRoundManager().getHost();
        eligiblePlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isHost = host != null && player.getUniqueId().equals(host.getUniqueId());
            boolean isSpectator = player.getGameMode() == GameMode.SPECTATOR;
            if (!isHost && !isSpectator) {
                eligiblePlayers.add(player.getUniqueId());
            }

            plugin.getFreezeManager().freeze(player);
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> plugin.getFreezeManager().unfreeze(player), FREEZE_TICKS);

            player.showTitle(Title.title(
                    Component.text(caller.getName() + " called a meeting", NamedTextColor.YELLOW),
                    Component.empty(),
                    Title.Times.times(Duration.ofMillis(300), Duration.ofSeconds(2), Duration.ofMillis(300))
            ));
            snapshotInventory(player);
            player.getInventory().clear();
        }

        phaseTask = Bukkit.getScheduler().runTaskLater(plugin, this::startVotingPhase, EXPLANATION_TICKS);
    }

    private void snapshotInventory(Player player) {
        PlayerInventory inv = player.getInventory();
        PlayerSnapshot snap = new PlayerSnapshot();
        snap.contents = inv.getContents().clone();
        snap.armor = inv.getArmorContents().clone();
        snap.offHand = inv.getItemInOffHand().clone();
        snapshots.put(player.getUniqueId(), snap);
    }

    private void restoreInventory(Player player) {
        PlayerSnapshot snap = snapshots.get(player.getUniqueId());
        if (snap == null) {
            return;
        }
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setContents(snap.contents);
        inv.setArmorContents(snap.armor);
        inv.setItemInOffHand(snap.offHand);
    }

    private void startVotingPhase() {
        votes.clear();
        for (UUID id : eligiblePlayers) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                player.getInventory().addItem(verdictBell.create(plugin));
                player.showTitle(Title.title(
                        Component.text("Voting has begun", NamedTextColor.AQUA),
                        Component.text("Right click the Verdict Bell to vote"),
                        Title.Times.times(Duration.ofMillis(300), Duration.ofSeconds(2), Duration.ofMillis(300))
                ));
            }
        }
        phaseTask = Bukkit.getScheduler().runTaskLater(plugin, this::resolveVoting, VOTING_TICKS);
    }

    public void castVote(Player voter, UUID target) {
        if (!meetingActive || !eligiblePlayers.contains(voter.getUniqueId())){
            return;
        }
        votes.put(voter.getUniqueId(),target);
        voter.closeInventory();

        if (votes.size() >= eligiblePlayers.size()) {
            if (phaseTask != null) {
                phaseTask.cancel();
                phaseTask = null;
            }
            resolveVoting();
        }
    }

    private void resolveVoting() {
        if (votes.isEmpty()) {
            broadcastTitle("Time Ran Out", "", NamedTextColor.GRAY, 3);
            Bukkit.getScheduler().runTaskLater(plugin, this::endMeeting, 20L * 3L);
            return;
        }

        Map<UUID, Integer>tally = new HashMap<>();
        for (UUID target : votes.values()) {
            tally.merge(target, 1, Integer::sum);
        }

        int maxVotes = 0;
        UUID leader = null;
        boolean tied = false;
        for (Map.Entry<UUID, Integer>entry : tally.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                leader = entry.getKey();
                tied = true;
            } else if (entry.getValue() == maxVotes) {
                tied = true;
            }
        }

        if (tied || leader == null || leader.equals(SKIP_VOTE)) {
            broadcastTitle("Skipped", "", NamedTextColor.GRAY, 3);
            Bukkit.getScheduler().runTaskLater(plugin, this::endMeeting, 20L * 3L);
            return;
        }

        Player eliminated = Bukkit.getPlayer(leader);
        if (eliminated == null) {
            endMeeting();
            return;
        }
        runEliminationReveal(eliminated);
    }

    private void runEliminationReveal(Player eliminated) {
        String name = eliminated.getName();
        boolean isPhantom = plugin.getPhantomManager().hasRole(eliminated.getUniqueId());
        boolean isShapeshifter = plugin.getShapeshiftManager().hasRole(eliminated.getUniqueId());
        String roleText = (isPhantom) ? "PHANTOM" : (isShapeshifter) ? "SHAPESHIFTER" : "innocent...";

        broadcastTitle(name, "", NamedTextColor.WHITE, 2);
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                broadcastTitle("Was...", "", NamedTextColor.WHITE, 2), 20L * 2L);
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                broadcastTitle(roleText, "", isPhantom || isShapeshifter ? NamedTextColor.DARK_RED : NamedTextColor.GREEN, 3), 20L * 4L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            eliminated.setHealth(0.0);

            boolean roundEnded = plugin.getRoundManager().checkWinCondition();
            if (!roundEnded) {
                endMeeting();
            } else {
                meetingActive = false;
                snapshots.clear();
                votes.clear();
            }
        }, 20L * 7L);
    }

    private void broadcastTitle(String main, String sub, NamedTextColor color, int seconds) {
        Title title = Title.title(
                Component.text(main, color),
                Component.text(sub),
                Title.Times.times(Duration.ofMillis(300), Duration.ofSeconds(seconds), Duration.ofMillis(300))
        );
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(title);
        }
    }

    private void endMeeting() {
        meetingActive = false;

        for (UUID id : eligiblePlayers) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) {
                continue;
            }
            restoreInventory(player);
            plugin.getGroupTimerManager().scatterPlayer(player);
        }

        snapshots.clear();
        votes.clear();
    }
}
