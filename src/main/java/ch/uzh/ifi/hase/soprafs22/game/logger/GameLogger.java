package ch.uzh.ifi.hase.soprafs22.game.logger;

import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameLogger;
import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameStatistics;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Implementation of GameLogger to provide GameStatistics.
 */
public final class GameLogger implements IGameStatistics, IGameLogger {
    private final Map<Long, Integer> unitsPerPlayer;

    private final List<Map<Long, Integer>> turnSnapshots = new ArrayList<>();
    private final Map<Long, List<Integer>> killsPerPlayer = new HashMap<>();
    private int totalMoves = 0;
    private int turn = 0;

    public GameLogger(Map<Long, Integer> unitsPerPlayer) {
        this.unitsPerPlayer = new HashMap<>();
        this.unitsPerPlayer.putAll(unitsPerPlayer);
        // Add a ghost entry with the initial state, so we can compute kills in first turn.
        turnSnapshots.add(Map.copyOf(unitsPerPlayer));
        for (long player : unitsPerPlayer.keySet())
            this.killsPerPlayer.put(player, new ArrayList<>());
        for (List<Integer> kills : this.killsPerPlayer.values())
            kills.add(0);
    }

    @Override
    public @NotNull Map<Long, List<Integer>> unitsPerPlayer() {
        Map<Long, List<Integer>> result = new HashMap<>();
        for(long player : unitsPerPlayer.keySet())
            result.put(player, new ArrayList<>());
        for (Map<Long, Integer> turnSnapshot : turnSnapshots)
            for (long player : turnSnapshot.keySet())
                result.get(player).add(turnSnapshot.get(player));
        return result;
    }

    @Override
    public Map<Long, List<Integer>> killsPerPlayer() {
        return killsPerPlayer;
    }

    @Override
    public float averageUnitsPerTurn() {
        int allUnitsInAllTurns = 0;
        boolean first = true;
        for(Map<Long, Integer> snapshot :  turnSnapshots) {
            // Ignore the first (ghost entry).
            if (first) {
                first = false;
                continue;
            }
            allUnitsInAllTurns += snapshot.values().stream().mapToInt(Integer::intValue).sum();
        }
        return (float) allUnitsInAllTurns / (float) (turnSnapshots.size() - 1); // -1 for the ghost entry.
    }

    private @NotNull List<Integer> killsPerTurn() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i < turnSnapshots.size(); i++) {
            // All the units that were alive in previous turn
            int prev = aliveUnitsAtTurn(i - 1);
            // All the units that are alive now
            int curr = aliveUnitsAtTurn(i);
            result.add(prev-curr);
        }
        return result;
    }

    private int aliveUnitsAtTurn(int turn) {
        return aliveUnits(turnSnapshots.get(turn));
    }

    @Contract(pure = true)
    private int aliveUnits(@NotNull Map<Long, Integer> snapshot) {
        int sum = 0;
        for(int units : snapshot.values())
            sum += units;
        return sum;
    }

    @Override
    public float averageKillsPerTurn() {
        return killsPerTurn().stream().mapToInt(Integer::intValue).sum() / (float)(turnSnapshots.size()-1);
    }

    @Override
    public int totalMoves() {
        return totalMoves;
    }

    @Override
    public void unitKilledAtTurn(int turn, long attackingPlayer, long defendingPlayer) {
        if (turn != this.turn)
            throw new IllegalStateException("Wrong turn!");
        unitsPerPlayer.replace(defendingPlayer, unitsPerPlayer.get(defendingPlayer) -1);
        List<Integer> kills = killsPerPlayer.get(attackingPlayer);
        kills.set(kills.size()-1, kills.get(kills.size()-1) + 1);
    }

    @Override
    public void move(int turn) {
        if (turn != this.turn)
            throw new IllegalStateException("Wrong turn!");
        totalMoves++;
    }

    @Override
    public void nextTurn() {
        // Take a snapshot with the units per player!
        turnSnapshots.add(Map.copyOf(unitsPerPlayer));
        // Add a new entry to kills per player.
        for (List<Integer> kills : killsPerPlayer.values()) {
            kills.add(0);
        }
        turn++;
    }
}
