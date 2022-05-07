package ch.uzh.ifi.hase.soprafs22.game.logger.implementation;

import ch.uzh.ifi.hase.soprafs22.game.logger.GameLogger;
import ch.uzh.ifi.hase.soprafs22.game.logger.GameStatistics;

import java.util.*;

/**
 * Implementation of GameLogger to provide GameStatistics.
 */
public final class GameLoggerImpl implements GameStatistics, GameLogger {
    private final Map<Long, Integer> unitsPerPlayer;

    private final List<Map<Long, Integer>> turnSnapshots = new ArrayList<>();
    private int totalMoves = 0;
    private int turn = 0;

    public static Builder newBuilder(){
        return new Builder();
    }

    public static final class Builder {
        Optional<Map<Long, Integer>> unitsPerPlayer = Optional.empty();

        private Builder() {}

        public Builder setUnitsPerPlayer(Map<Long, Integer> unitsPerPlayer){
            this.unitsPerPlayer = Optional.of(unitsPerPlayer);
            return this;
        }

        public GameLoggerImpl build() {
            if (unitsPerPlayer.isEmpty())
                throw new RuntimeException("Missing fields");
            return new GameLoggerImpl(Map.copyOf(unitsPerPlayer.get()));
        }
    }


    private GameLoggerImpl(Map<Long, Integer> unitsPerPlayer) {
        this.unitsPerPlayer = unitsPerPlayer;
        // Add a ghost entry with the initial state, so we can compute kills in first turn.
        turnSnapshots.add(Map.copyOf(unitsPerPlayer));
    }

    @Override
    public Map<Long, List<Integer>> unitsPerPlayer() {
        Map<Long, List<Integer>> result = new HashMap<>();
        for(long player : unitsPerPlayer.keySet())
            result.put(player, new ArrayList<>());
        for (Map<Long, Integer> turnSnapshot : turnSnapshots)
            for (long player : turnSnapshot.keySet())
                result.get(player).add(turnSnapshot.get(player));
        return result;
    }

    @Override
    public float averageUnitsPerTurn() {
        int allUnitsInAllTurns = turnSnapshots.stream().flatMap(snapshot -> snapshot.values().stream()).mapToInt(Integer::intValue).sum();
        return (float) allUnitsInAllTurns / (float) (turnSnapshots.size() - 1); // -1 for the ghost entry.
    }

    private List<Integer> killsPerTurn() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i < turnSnapshots.size(); i++) {
            // All the units that were alive in previous turn
            int prev = turnSnapshots.get(i - 1).values().stream().mapToInt(Integer::intValue).sum();
            // All the units that are alive now
            int curr = turnSnapshots.get(i).values().stream().mapToInt(Integer::intValue).sum();
            result.add(prev-curr);
        }
        return result;
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
    public void unitKilledAtTurn(int turn, long playerId) {
        if (turn != this.turn)
            throw new IllegalStateException("Wrong turn!");
        unitsPerPlayer.replace(playerId, unitsPerPlayer.get(playerId) -1);
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
        turn++;
    }
}
