package ch.uzh.ifi.hase.soprafs22.game;

import java.util.List;
import java.util.Map;

public class SurrenderInfo {
    private final long surrenderedPlayer;
    private Map<Long, List<Integer>> rankedScoreDeltas;

    public SurrenderInfo(long surrenderedPlayer) {
        this.surrenderedPlayer = surrenderedPlayer;
    }

    public SurrenderInfo(long surrenderedPlayer, Map<Long, List<Integer>> rankedScoreDeltas) {
        this.surrenderedPlayer = surrenderedPlayer;
        this.rankedScoreDeltas = rankedScoreDeltas;
    }

    public long getSurrenderedPlayer() {
        return surrenderedPlayer;
    }
}
