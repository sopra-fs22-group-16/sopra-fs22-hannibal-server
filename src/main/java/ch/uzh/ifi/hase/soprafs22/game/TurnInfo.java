package ch.uzh.ifi.hase.soprafs22.game;

import java.util.Optional;

/**
 * Holds data related to a turn.
 */
public class TurnInfo {
    private final int turn;
    private final long playerId;

    private TurnInfo(int turn, long playerId) {
        this.turn = turn;
        this.playerId = playerId;
    }

    /**
     * Get the turn number.
     * @return the turn number.
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Get the player Id whose turn it is.
     * @return the id of the player whose turn it is.
     */
    public long getPlayerId() {
        return playerId;
    }

    /**
     * @return a new builder for TurnInfo.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * A builder for {@link TurnInfo}.
     */
    public static class Builder {
        private Optional<Integer> turn = Optional.empty();
        private Optional<Long> playerId = Optional.empty();

        private Builder() {}

        public Builder setTurn(int turn) {
            this.turn = Optional.of(turn);
            return this;
        }

        public Builder setPlayerId(long playerId) {
            this.playerId = Optional.of(playerId);
            return this;
        }

        /**
         * Builds a {@link TurnInfo}, will throw RuntimeException if not all fields are set.
         */
        public TurnInfo build() {
            if (turn.isEmpty() || playerId.isEmpty())
                throw new RuntimeException("Missing fields");
            return new TurnInfo(turn.get(), playerId.get());
        }
    }
}
