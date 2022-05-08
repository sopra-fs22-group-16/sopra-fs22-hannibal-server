package ch.uzh.ifi.hase.soprafs22.game.logger.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Provides statistical information to the client about a given game.
 */
public interface IGameStatistics {
    /**
     * Returns the number of life units per player at each turn.
     */
    Map<Long, List<Integer>> unitsPerPlayer();

    /**
     * Average alive units at every turn.
     */
    float averageUnitsPerTurn();

    /**
     * Average units killed per turn.
     */
    float averageKillsPerTurn();

    /**
     * Total number of moves
     */
    int totalMoves();
}
