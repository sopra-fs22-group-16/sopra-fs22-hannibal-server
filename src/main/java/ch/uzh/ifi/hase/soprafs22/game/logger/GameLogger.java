package ch.uzh.ifi.hase.soprafs22.game.logger;

/**
 * A Logger for {@link ch.uzh.ifi.hase.soprafs22.game.Game}.
 */
public interface GameLogger {

    /**
     * Inform that a unit was killed.
     * @param turn in which it was killed.
     * @param playerId id of player that lost the unit.
     */
    void unitKilledAtTurn(int turn, long playerId);

    /**
     * Inform that a unit moved.
     * @param turn in which it moved.
     */
    void move(int turn);

    /**
     * Inform that turn is over.
     */
    void nextTurn();
}
