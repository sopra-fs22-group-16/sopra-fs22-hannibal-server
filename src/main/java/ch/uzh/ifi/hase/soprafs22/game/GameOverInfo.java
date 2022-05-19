package ch.uzh.ifi.hase.soprafs22.game;

import java.util.Collection;
import java.util.List;

public class GameOverInfo {
    private final List<Long> winners;
    private SurrenderInfo surrenderInfo;

    public GameOverInfo(Collection<Long> winners) {
        this.winners = List.copyOf(winners);
    }

    public List<Long> getWinners() {
        return winners;
    }

    public SurrenderInfo getSurrenderInfo() {
        return surrenderInfo;
    }

    public void setSurrenderInfo(SurrenderInfo surrenderInfo) {
        this.surrenderInfo = surrenderInfo;
    }
}
