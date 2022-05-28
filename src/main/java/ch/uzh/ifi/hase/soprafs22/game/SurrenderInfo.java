package ch.uzh.ifi.hase.soprafs22.game;

import java.util.List;
import java.util.Map;

public class SurrenderInfo {
    private final long surrenderedPlayer;
    private final Map<Long, List<Integer>> rankedScoreDeltas;

    public SurrenderInfo(long surrenderedPlayer, Map<Long, List<Integer>> rankedScoreDeltas) {
        this.surrenderedPlayer = surrenderedPlayer;
        this.rankedScoreDeltas = rankedScoreDeltas;
    }

    public long getSurrenderedPlayer() {
        return surrenderedPlayer;
    }

    public Map<Long, List<Integer>> getRankedScoreDeltas() {
        return rankedScoreDeltas;
    }
}
