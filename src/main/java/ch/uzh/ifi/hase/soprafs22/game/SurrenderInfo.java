package ch.uzh.ifi.hase.soprafs22.game;

public class SurrenderInfo {
    private final long surrenderedPlayer;

    public SurrenderInfo(long surrenderedPlayer) {
        this.surrenderedPlayer = surrenderedPlayer;
    }

    public long getSurrenderedPlayer() {
        return surrenderedPlayer;
    }
}
