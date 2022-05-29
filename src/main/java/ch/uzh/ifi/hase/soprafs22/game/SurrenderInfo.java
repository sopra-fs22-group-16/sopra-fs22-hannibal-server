package ch.uzh.ifi.hase.soprafs22.game;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurrenderInfo that = (SurrenderInfo) o;
        return surrenderedPlayer == that.surrenderedPlayer && Objects.equals(rankedScoreDeltas, that.rankedScoreDeltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surrenderedPlayer, rankedScoreDeltas);
    }

    @Override
    public String toString() {
        return "SurrenderInfo{" +
                "surrenderedPlayer=" + surrenderedPlayer +
                ", rankedScoreDeltas=" + rankedScoreDeltas +
                '}';
    }
}
