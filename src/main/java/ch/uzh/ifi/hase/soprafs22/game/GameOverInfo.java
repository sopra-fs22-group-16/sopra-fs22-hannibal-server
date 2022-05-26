package ch.uzh.ifi.hase.soprafs22.game;

import java.util.List;
import java.util.Map;

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
}
