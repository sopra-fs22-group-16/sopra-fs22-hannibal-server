package ch.uzh.ifi.hase.soprafs22.game;

import java.util.Objects;

/**
 * Holds data related to a turn.
 */
public class TurnInfo {
    private final int turn;
    private final long playerId;

    public TurnInfo(int turn, long playerId) {
        this.turn = turn;
        this.playerId = playerId;
    }

    /**
     * Get the turn number.
     *
     * @return the turn number.
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Get the player's ID whose turn it is.
     *
     * @return the id of the player whose turn it is.
     */
    public long getPlayerId() {
        return playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnInfo turnInfo = (TurnInfo) o;
        return turn == turnInfo.turn && playerId == turnInfo.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, playerId);
    }

    @Override
    public String toString() {
        return "TurnInfo{" +
                "turn=" + turn +
                ", playerId=" + playerId +
                '}';
    }
}
