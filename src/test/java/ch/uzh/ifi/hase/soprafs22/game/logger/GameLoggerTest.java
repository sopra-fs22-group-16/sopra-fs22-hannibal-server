package ch.uzh.ifi.hase.soprafs22.game.logger;

import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameLoggerTest {

    private GameLogger gameLogger;
    private static final Map<Long, Integer> INITIAL_PLAYER_UNITS = Map.of(1L, 10, 2L, 8);

    @BeforeEach
    void setup() {
        gameLogger = new GameLogger(Map.copyOf(INITIAL_PLAYER_UNITS));
    }

    @Test
    void unitKilledAtTurn_wrongTurn() {
        assertThrows(IllegalStateException.class, () ->gameLogger.unitKilledAtTurn(1,1L));
    }

    @Test
    void move_wrongTurn() {
        assertThrows(IllegalStateException.class, () ->gameLogger.move(1));
    }

    private static void logGame(IGameLogger gameLogger) {
        // Turn 0
        gameLogger.unitKilledAtTurn(0, 2L);
        gameLogger.move(0);
        gameLogger.move(0);
        gameLogger.nextTurn();

        // Turn 1
        gameLogger.unitKilledAtTurn(1, 1L);
        gameLogger.unitKilledAtTurn(1, 1L);
        gameLogger.move(1);
        gameLogger.move(1);
        gameLogger.move(1);
        gameLogger.nextTurn();


        // Turn 2
        gameLogger.unitKilledAtTurn(2, 2L);
        gameLogger.move(2);
        gameLogger.nextTurn();
    }

    @Test
    void unitsPerPlayer() {
        logGame(gameLogger);

        Map<Long, List<Integer>> units = gameLogger.unitsPerPlayer();

        assertEquals(List.of(10, 10, 8, 8), units.get(1L));
        assertEquals(List.of(8, 7, 7, 6), units.get(2L));
    }

    @Test
    void averageUnitsPerTurn() {
        logGame(gameLogger);

        float avgUnits = gameLogger.averageUnitsPerTurn();

        float turns = 3;
        float sumOfunits = 17 + 15 + 14;
        float expected = sumOfunits / turns;
        assertEquals(expected, avgUnits);
    }

    @Test
    void averageKillsPerTurn() {
        logGame(gameLogger);

        float avgKill = gameLogger.averageKillsPerTurn();

        assertEquals((4.f/3.f), avgKill);
    }

    @Test
    void totalMoves() {
        logGame(gameLogger);

        int moves = gameLogger.totalMoves();

        assertEquals(6, moves);
    }
}
