package ch.uzh.ifi.hase.soprafs22.game.logger.interfaces;

/**
 * A Logger for {@link ch.uzh.ifi.hase.soprafs22.game.Game}.
 */
public interface IGameLogger {

    /**
     * Inform that a unit was killed.
     * @param turn in which it was killed.
     * @param attackingPlayer id of player that killed the unit.
     * @param defendingPlayer id of player that lost the unit.
     */
    void unitKilledAtTurn(int turn, long attackingPlayer, long defendingPlayer);

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
