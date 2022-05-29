package ch.uzh.ifi.hase.soprafs22.game;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameOverInfo {
    private final List<Long> winners;
    private final Map<Long, List<Integer>> rankedScoreDeltas;

    public GameOverInfo(List<Long> winners) {
        this.winners = List.copyOf(winners);
        this.rankedScoreDeltas = null;
    }

    public GameOverInfo(List<Long> winners, Map<Long, List<Integer>> rankedScoreDeltas) {
        this.winners = winners;
        this.rankedScoreDeltas = rankedScoreDeltas;
    }

    public List<Long> getWinners() {
        return winners;
    }

    public Map<Long, List<Integer>> getRankedScoreDeltas() {
        return rankedScoreDeltas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameOverInfo that = (GameOverInfo) o;
        return Objects.equals(winners, that.winners) && Objects.equals(rankedScoreDeltas, that.rankedScoreDeltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winners, rankedScoreDeltas);
    }

    @Override
    public String toString() {
        return "GameOverInfo{" +
                "winners=" + winners +
                ", rankedScoreDeltas=" + rankedScoreDeltas +
                '}';
    }
}
