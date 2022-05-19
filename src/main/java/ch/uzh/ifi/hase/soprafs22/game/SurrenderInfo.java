package ch.uzh.ifi.hase.soprafs22.game;

public class SurrenderInfo {
    private final long surrenderedPlayer;

    public SurrenderInfo(long surrenderedPlayerId) {
        this.surrenderedPlayer = surrenderedPlayerId;
    }

    public long getSurrenderedPlayer() {
        return surrenderedPlayer;
    }
}
